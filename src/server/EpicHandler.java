package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import enums.Endpoint;
import exceptions.ManagerAddTaskException;
import interfaces.TaskManager;
import server.typeAdapter.DurationAdapter;
import server.typeAdapter.LocalDateTimeAdapter;
import tasks.Epic;
import tasks.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class EpicHandler extends TaskHandler {
    public final Gson gson = new GsonBuilder()
            .serializeNulls()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    public EpicHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_TASKS: {
                getTask(exchange, manager.getListEpic());
            }
            case GET_TASK_BY_ID: {
                if (getTaskById(exchange, manager.getListEpic())) {
                    String body = gson.toJson(manager.getEpicById(Integer.parseInt(pathParts[2])));
                    sendText(exchange, body);
                }
            }
            case GET_SUBTASK_BY_EPIC: {
                if (getSubtask(exchange, manager.getListEpic())) {
                    String body = gson.toJson(manager.getListSubtaskToEpic(Integer.parseInt(pathParts[2])));
                    sendText(exchange, body);
                }
            }
            case POST_TASK: {
                postTask(exchange, manager.getListEpic());
            }
            case DELETE_TASK_BY_ID: {
                if (deleteTaskById(exchange, manager.getListEpic())) {
                    manager.removeEpicById(Integer.parseInt(pathParts[2]));
                    sendCode(exchange);
                }
            }
            default: {
                sendNotFound(exchange);
            }
        }
    }

    @Override
    public void postTask(HttpExchange exchange, List<? extends Task> list) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

        JsonElement jsonElement = JsonParser.parseString(body);

        if (!jsonElement.isJsonObject()) {
            sendServerError(exchange);
            return;
        }

        Epic task = gson.fromJson(body, Epic.class);

        try {
            if (list.contains(task)) {
                manager.updateEpic(task);
            } else {
                manager.createEpic(task);
            }
            sendCode(exchange);
        } catch (ManagerAddTaskException e) {
            sendHasOverlaps(exchange);
        }
    }

    private boolean getSubtask(HttpExchange exchange, List<Epic> list) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");

        if (!pathParts[3].equals("subtasks")) {
            sendHasOverlaps(exchange);
            return false;
        }

        if (list.isEmpty()) {
            sendNotFound(exchange);
            return false;
        }

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
}