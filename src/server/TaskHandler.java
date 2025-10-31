package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import enums.Endpoint;
import exceptions.ManagerAddTaskException;
import interfaces.TaskManager;
import server.typeAdapter.DurationAdapter;
import server.typeAdapter.LocalDateTimeAdapter;
import tasks.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {
    public final Gson gson = new GsonBuilder()
            .serializeNulls()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();
    public final TaskManager manager;

    public TaskHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_TASKS: {
                getTask(exchange, manager.getListTask());
            }
            case GET_TASK_BY_ID: {
                if (getTaskById(exchange, manager.getListTask())) {
                    String body = gson.toJson(manager.getTaskById(Integer.parseInt(pathParts[2])));
                    sendText(exchange, body);
                }
            }
            case POST_TASK: {
                postTask(exchange, manager.getListTask());
            }
            case DELETE_TASK_BY_ID: {
                if (deleteTaskById(exchange, manager.getListTask())) {
                    manager.removeTaskById(Integer.parseInt(pathParts[2]));
                    sendCode(exchange);
                }
            }
            default: {
                sendNotFound(exchange);
            }
        }
    }

    public Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        switch (requestMethod) {
            case "GET" -> {
                if (pathParts.length == 2) {
                    return Endpoint.GET_TASKS;
                } else if (pathParts.length == 3) {
                    return Endpoint.GET_TASK_BY_ID;
                } else {
                    return Endpoint.GET_SUBTASK_BY_EPIC;
                }
            }
            case "POST" -> {
                return Endpoint.POST_TASK;
            }
            case "DELETE" -> {
                return Endpoint.DELETE_TASK_BY_ID;
            }
            default -> {
                return Endpoint.UNKNOWN;
            }
        }
    }

    public void getTask(HttpExchange exchange, List<? extends Task> list) throws IOException {
        if (list.isEmpty()) {
            sendNotFound(exchange);
            return;
        }

        String body = gson.toJson(list);
        sendText(exchange, body);
    }

    public boolean getTaskById(HttpExchange exchange, List<? extends Task> list) throws IOException {
        Optional<Integer> idOpt = getIdOpt(exchange);

        if (idOpt.isEmpty()) {
            sendHasOverlaps(exchange);
            return false;
        }

        int id = idOpt.get();

        for (Task task : list) {
            if (task.getId() == id) {
                return true;
            }
        }

        sendNotFound(exchange);
        return false;
    }

    public void postTask(HttpExchange exchange, List<? extends Task> list) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

        JsonElement jsonElement = JsonParser.parseString(body);

        if (!jsonElement.isJsonObject()) {
            sendServerError(exchange);
            return;
        }

        Task task = gson.fromJson(body, Task.class);

        try {
            if (list.contains(task)) {
                manager.updateTask(task);
            } else {
                manager.createTask(task);
            }
            sendCode(exchange);
        } catch (ManagerAddTaskException e) {
            sendHasOverlaps(exchange);
        }
    }

    public boolean deleteTaskById(HttpExchange exchange, List<? extends Task> list) throws IOException {
        Optional<Integer> idOpt = getIdOpt(exchange);

        if (idOpt.isEmpty()) {
            sendHasOverlaps(exchange);
            return false;
        }

        int id = idOpt.get();

        for (Task task : list) {
            if (task.getId() == id) {
                return true;
            }
        }

        sendNotFound(exchange);
        return false;
    }

    public Optional<Integer> getIdOpt(HttpExchange exchange) {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        try {
            return Optional.of(Integer.parseInt(pathParts[2]));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }
}