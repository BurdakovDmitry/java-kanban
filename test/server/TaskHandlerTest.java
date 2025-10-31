package server;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import enums.StatusTask;
import interfaces.TaskManager;
import managers.Managers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TaskHandlerTest {
    private final File file = File.createTempFile("testFile", ".txt");
    private final TaskManager manager = Managers.getDefault(file);
    private final HttpTaskServer server = new HttpTaskServer(manager);
    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = server.getGson();
    private final Task task1 = new Task("Task1", StatusTask.NEW, "Description1",
            LocalDateTime.of(2025, 10, 10, 12, 25), Duration.ofMinutes(60));
    private final Task task2 = new Task("Task2", StatusTask.NEW, "Description2",
            LocalDateTime.of(2025, 10, 10, 15, 25), Duration.ofMinutes(60));
    private final Task task3 = new Task("Task3", StatusTask.NEW, "Description2",
            LocalDateTime.of(2025, 10, 10, 12, 45), Duration.ofMinutes(60));


    public TaskHandlerTest() throws IOException {
    }

    @BeforeEach
    public void start() {
        server.start();
    }

    @AfterEach
    public void stop() {
        server.stop();
    }

    @Test
    public void testGetTask() throws IOException, InterruptedException {
        manager.createTask(task1);
        manager.createTask(task2);

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonElement jsonElement = JsonParser.parseString(response.body());

        final List<Task> list = gson.fromJson(jsonElement, new TypeToken<List<Task>>() {
        }.getType());

        assertEquals(200, response.statusCode(), "Код не соответствует");
        assertEquals(2, list.size(), "Некорректное количество задач");
    }

    @Test
    public void testGetTaskListEmpty() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Код не соответствует");
    }

    @Test
    public void testGetTaskById() throws IOException, InterruptedException {
        manager.createTask(task1);

        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonElement jsonElement = JsonParser.parseString(response.body());

        final Task task = gson.fromJson(jsonElement, Task.class);

        assertEquals(200, response.statusCode(), "Код не соответствует");
        assertEquals(task1, task, "Задачи не совпадают");
    }

    @Test
    public void testGetTaskByIdNotFound() throws IOException, InterruptedException {
        manager.createTask(task1);

        URI url = URI.create("http://localhost:8080/tasks/2");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Код не соответствует");
    }

    @Test
    public void testGetTaskByIdErrorSyntax() throws IOException, InterruptedException {
        manager.createTask(task1);

        URI url = URI.create("http://localhost:8080/tasks/hallo");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response.statusCode(), "Код не соответствует");
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        String taskJson = gson.toJson(task1);

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        final List<Task> list = manager.getListTask();

        assertNotNull(list, "Задачи не возвращаются");
        assertEquals(1, list.size(), "Некорректное количество задач");
        assertEquals("Task1", list.getFirst().getNameTask(), "Некорректное имя задачи");
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {
        manager.createTask(task1);
        final int id = task1.getId();

        final Task task4 = new Task(id, "Task4", StatusTask.NEW, "Description4",
                LocalDateTime.of(2025, 10, 10, 12, 25), Duration.ofMinutes(60));
        String taskJson = gson.toJson(task4);

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        final List<Task> list = manager.getListTask();

        assertNotNull(list, "Задачи не возвращаются");
        assertEquals(1, list.size(), "Некорректное количество задач");
        assertEquals("Task4", list.getFirst().getNameTask(), "Некорректное имя задачи");
    }

    @Test
    public void testAddTaskManagerAddTaskException() throws IOException, InterruptedException {
        manager.createTask(task1);
        String taskJson = gson.toJson(task3);

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response.statusCode());
    }

    @Test
    public void testAddTaskJsonSyntaxException() throws IOException, InterruptedException {
        String taskJson = gson.toJson("Test String");

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(500, response.statusCode());
    }

    @Test
    public void testDeleteTaskById() throws IOException, InterruptedException {
        manager.createTask(task1);
        manager.createTask(task2);

        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        final List<Task> list = manager.getListTask();

        assertEquals(201, response.statusCode(), "Код не соответствует");
        assertEquals(1, list.size(), "Некорректное количество задач");
        assertEquals("Task2", list.getFirst().getNameTask(), "Некорректное имя задачи");
    }

    @Test
    public void testDeleteTaskByIdNotFound() throws IOException, InterruptedException {
        manager.createTask(task1);

        URI url = URI.create("http://localhost:8080/tasks/2");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Код не соответствует");
    }

    @Test
    public void testDeleteTaskByIdErrorSyntax() throws IOException, InterruptedException {
        manager.createTask(task1);

        URI url = URI.create("http://localhost:8080/tasks/hallo");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response.statusCode(), "Код не соответствует");
    }
}