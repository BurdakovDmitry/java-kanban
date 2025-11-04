package server;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class BaseHttpHandler {
    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendCode(HttpExchange h) throws IOException {
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(201, 0);
        h.close();
    }

    protected void sendNotFound(HttpExchange h) throws IOException {
        byte[] resp = "Not Found".getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(404, 0);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendHasOverlaps(HttpExchange h) throws IOException {
        byte[] resp = "Not Acceptable".getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(406, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendServerError(HttpExchange h) throws IOException {
        byte[] resp = "Internal Server Error".getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(500, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }
}