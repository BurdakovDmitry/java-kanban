package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import interfaces.TaskManager;
import server.typeAdapter.DurationAdapter;
import server.typeAdapter.LocalDateTimeAdapter;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PrioritizedHandler  extends BaseHttpHandler implements HttpHandler {
    public final Gson gson = new GsonBuilder()
            .serializeNulls()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();
    public final TaskManager manager;

    public PrioritizedHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String pathParts = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        if (method.equals("GET") && pathParts.equals("/prioritized")) {
            List<Task> list = new ArrayList<>();

            if (manager.getPrioritizedTasks().isEmpty()) {
                list = loadPrioritizedTasks();
            } else {
                String body = gson.toJson(manager.getPrioritizedTasks());
                sendText(exchange, body);
            }

            if (list.isEmpty()) {
                sendNotFound(exchange);
                return;
            }

            String body = gson.toJson(list);
            sendText(exchange, body);
        } else {
            sendHasOverlaps(exchange);
        }
    }

    private List<Task> loadPrioritizedTasks() {
        List<Task> tasks = manager.getListTask().stream()
                .filter(task -> task.getStartTime() != null)
                .toList();
        List<Subtask> subtasks = manager.getListSubtask().stream()
                .filter(subtask -> subtask.getStartTime() != null)
                .toList();

        return Stream.of(tasks, subtasks)
                .filter(task -> !task.isEmpty())
                .flatMap(Collection::stream)
                .sorted(Comparator.comparing(Task::getStartTime))
                .collect(Collectors.toList());
    }
}