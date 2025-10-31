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

class HistoryHandlerTest {
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
            LocalDateTime.of(2025, 10, 10, 13, 45), Duration.ofMinutes(60));

    public HistoryHandlerTest() throws IOException {
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
    public void testGetHistory() throws IOException, InterruptedException {
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createTask(task3);

        manager.getTaskById(2);
        manager.getTaskById(1);
        manager.getTaskById(3);

        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonElement jsonElement = JsonParser.parseString(response.body());

        final List<Task> listTask = gson.fromJson(jsonElement, new TypeToken<List<Task>>() {
        }.getType());

        assertEquals(200, response.statusCode(), "Код не соответствует");
        assertEquals(3, listTask.size(), "Некорректное количество задач");
        assertEquals(task2, listTask.getFirst(), "Некорректное имя задачи");
        assertEquals(task1, listTask.get(1), "Некорректное имя задачи");
        assertEquals(task3, listTask.getLast(), "Некорректное имя задачи");
    }

    @Test
    public void testGetHistoryEmpty() throws IOException, InterruptedException {
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createTask(task3);

        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Код не соответствует");
    }

    @Test
    public void testGetHistoryError() throws IOException, InterruptedException {
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createTask(task3);

        URI url = URI.create("http://localhost:8080/history/error");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response.statusCode(), "Код не соответствует");
    }
}