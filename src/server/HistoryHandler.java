package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import interfaces.TaskManager;
import server.typeAdapter.DurationAdapter;
import server.typeAdapter.LocalDateTimeAdapter;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {
    public final Gson gson = new GsonBuilder()
            .serializeNulls()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();
    public final TaskManager manager;

    public HistoryHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String pathParts = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        if (pathParts == null) {
            sendNotFound(exchange);
            return;
        }

        if (method.equals("GET") && pathParts.equals("/history")) {
            if (manager.getHistory().isEmpty()) {
                sendNotFound(exchange);
                return;
            }

            String body = gson.toJson(manager.getHistory());
            sendText(exchange, body);
        } else {
            sendHasOverlaps(exchange);
        }
    }
}