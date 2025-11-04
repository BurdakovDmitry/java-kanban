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

class EpicHandlerTest {
    private final File file = File.createTempFile("testFile", ".txt");
    private final TaskManager manager = Managers.getDefault(file);
    private final HttpTaskServer server = new HttpTaskServer(manager);
    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = server.getGson();
    private final Epic epic1 = new Epic("Epic1", StatusTask.NEW, "Description1");
    private final Epic epic2 = new Epic("Epic2", StatusTask.NEW, "Description2");
    private final Subtask subtask1 = new Subtask(1, "Subtask1", StatusTask.NEW, "Description1",
            LocalDateTime.of(2025, 10, 10, 12, 25), Duration.ofMinutes(60));
    private final Subtask subtask2 = new Subtask(1, "Subtask2", StatusTask.NEW, "Description2",
            LocalDateTime.of(2025, 10, 10, 15, 25), Duration.ofMinutes(60));

    public EpicHandlerTest() throws IOException {
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
        manager.createEpic(epic2);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonElement jsonElement = JsonParser.parseString(response.body());

        final List<Epic> listTask = gson.fromJson(jsonElement, new TypeToken<List<Epic>>() {
        }.getType());

        assertEquals(200, response.statusCode(), "Код не соответствует");
        assertEquals(2, listTask.size(), "Некорректное количество задач");
    }

    @Test
    public void testGetSubtaskByEpic() throws IOException, InterruptedException {
        manager.createEpic(epic1);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        URI url = URI.create("http://localhost:8080/epics/1/subtasks");
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
    public void testGetSubtaskByEpicNotFound() throws IOException, InterruptedException {
        manager.createEpic(epic1);
        manager.createSubtask(subtask1);

        URI url = URI.create("http://localhost:8080/epics/10/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Код не соответствует");
    }

    @Test
    public void testGetSubtaskByEpicErrorSyntaxId() throws IOException, InterruptedException {
        manager.createEpic(epic1);
        manager.createSubtask(subtask1);

        URI url = URI.create("http://localhost:8080/epics/error/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response.statusCode(), "Код не соответствует");
    }

    @Test
    public void testGetSubtaskByEpicErrorSyntaxSubtasks() throws IOException, InterruptedException {
        manager.createEpic(epic1);
        manager.createSubtask(subtask1);

        URI url = URI.create("http://localhost:8080/epics/1/error");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response.statusCode(), "Код не соответствует");
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        String taskJson = gson.toJson(epic1);

        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        final List<Epic> list = manager.getListEpic();

        assertNotNull(list, "Задачи не возвращаются");
        assertEquals(1, list.size(), "Некорректное количество задач");
        assertEquals("Epic1", list.getFirst().getNameTask(), "Некорректное имя задачи");
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {
        manager.createEpic(epic1);
        manager.createTask(subtask1);
        final int id = epic1.getId();

        final Epic epic4 = new Epic(id, "Epic4", StatusTask.NEW, "Description4",
                LocalDateTime.now(), Duration.ofMinutes(60));
        String taskJson = gson.toJson(epic4);

        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        final List<Epic> list = manager.getListEpic();

        assertNotNull(list, "Задачи не возвращаются");
        assertEquals(1, list.size(), "Некорректное количество задач");
        assertEquals("Epic4", list.getFirst().getNameTask(), "Некорректное имя задачи");
    }

    @Test
    public void testAddTaskJsonSyntaxException() throws IOException, InterruptedException {
        String taskJson = gson.toJson("Test String");

        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(500, response.statusCode());
    }
}