package server;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import enums.Endpoint;
import exceptions.ManagerAddTaskException;
import interfaces.TaskManager;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SubtaskHandler extends TaskHandler {

    public SubtaskHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_TASKS: {
                getTask(exchange, manager.getListSubtask());
            }
            case GET_TASK_BY_ID: {
                if (getTaskById(exchange, manager.getListSubtask())) {
                    String body = gson.toJson(manager.getSubtaskById(Integer.parseInt(pathParts[2])));
                    sendText(exchange, body);
                }
            }
            case POST_TASK: {
                postTask(exchange, manager.getListSubtask());
            }
            case DELETE_TASK_BY_ID: {
                if (deleteTaskById(exchange, manager.getListSubtask())) {
                    manager.removeSubtaskById(Integer.parseInt(pathParts[2]));
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

        Subtask task = gson.fromJson(body, Subtask.class);

        try {
            if (list.contains(task)) {
                manager.updateSubtask(task);
            } else {
                manager.createSubtask(task);
            }
            sendCode(exchange);
        } catch (ManagerAddTaskException e) {
            sendHasOverlaps(exchange);
        }
    }
}