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
import tasks.Epic;
import tasks.Subtask;

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

class SubtaskHandlerTest {
    private final File file = File.createTempFile("testFile", ".txt");
    private final TaskManager manager = Managers.getDefault(file);
    private final HttpTaskServer server = new HttpTaskServer(manager);
    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = server.getGson();
    private final Epic epic1 = new Epic("Epic1", StatusTask.NEW, "Description1");
    private final Subtask subtask1 = new Subtask(1, "Subtask1", StatusTask.NEW, "Description1",
            LocalDateTime.of(2025, 10, 10, 12, 25), Duration.ofMinutes(60));
    private final Subtask subtask2 = new Subtask(1, "Subtask2", StatusTask.NEW, "Description2",
            LocalDateTime.of(2025, 10, 10, 15, 25), Duration.ofMinutes(60));
    private final Subtask subtask3 = new Subtask(1, "Subtask3", StatusTask.NEW, "Description2",
            LocalDateTime.of(2025, 10, 10, 12, 45), Duration.ofMinutes(60));


    public SubtaskHandlerTest() throws IOException {
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
        manager.createEpic(epic1);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonElement jsonElement = JsonParser.parseString(response.body());

        final List<Subtask> list = gson.fromJson(jsonElement, new TypeToken<List<Subtask>>() {
        }.getType());

        assertEquals(200, response.statusCode(), "Код не соответствует");
        assertEquals(2, list.size(), "Некорректное количество задач");
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        manager.createEpic(epic1);
        String taskJson = gson.toJson(subtask1);

        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        final List<Subtask> list = manager.getListSubtask();

        assertNotNull(list, "Задачи не возвращаются");
        assertEquals(1, list.size(), "Некорректное количество задач");
        assertEquals("Subtask1", list.getFirst().getNameTask(), "Некорректное имя задачи");
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {
        manager.createEpic(epic1);
        manager.createTask(subtask1);
        final int id = subtask1.getId();

        final Subtask subtask4 = new Subtask(id, "Subtask4", StatusTask.NEW, "Description4",
                LocalDateTime.of(2025, 10, 10, 12, 25),
                Duration.ofMinutes(60), 1);
        String taskJson = gson.toJson(subtask4);

        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        final List<Subtask> list = manager.getListSubtask();

        assertNotNull(list, "Задачи не возвращаются");
        assertEquals(1, list.size(), "Некорректное количество задач");
        assertEquals("Subtask4", list.getFirst().getNameTask(), "Некорректное имя задачи");
    }

    @Test
    public void testAddTaskManagerAddTaskException() throws IOException, InterruptedException {
        manager.createEpic(epic1);
        manager.createTask(subtask1);
        String taskJson = gson.toJson(subtask3);

        URI url = URI.create("http://localhost:8080/subtasks");
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

        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(500, response.statusCode());
    }
}