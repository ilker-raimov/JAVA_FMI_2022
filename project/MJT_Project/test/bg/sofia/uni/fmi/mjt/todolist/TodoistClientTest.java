package bg.sofia.uni.fmi.mjt.todolist;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class TodoistClientTest {
    private static final int PORT = 9999;
    private static final String ERROR_MESSAGE1 = "Starting client while the server" +
            " has not been started should throw a runtime exception";
    private static final String ERROR_MESSAGE2 = "Starting client while the server" +
            " has been started should not throw a runtime exception";

    @Test
    void testStartClientWithoutServerStarted() {
        TodolistClient client = new TodolistClient(PORT);

        assertThrows(RuntimeException.class, () -> client.start(), ERROR_MESSAGE1);
    }

    @Test
    void testStartClientAndServerStarted() {
        TodolistServer server = TodolistServer.getInstance(PORT);
        TodolistClient client = new TodolistClient(PORT);

        Thread serverThread = new Thread(server);
        Thread clientThread = new Thread(client);

        serverThread.start();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        assertDoesNotThrow(() -> clientThread.start(), ERROR_MESSAGE2);

        server.stopServer();
    }
}
