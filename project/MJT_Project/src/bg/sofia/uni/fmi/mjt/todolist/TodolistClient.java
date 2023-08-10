package bg.sofia.uni.fmi.mjt.todolist;

import bg.sofia.uni.fmi.mjt.todolist.exceptions.InvalidServerResponseException;
import bg.sofia.uni.fmi.mjt.todolist.response.ServerResponse;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class TodolistClient extends Thread {
    private static final int BUFFER_SIZE = 1024;
    private static final String HOST = "localhost";
    private ByteBuffer buffer;
    private final int port;

    public TodolistClient(int port) {
        this.port = port;
    }

    public static void main(String[] args) {
        TodolistClient client = new TodolistClient(16868);
        client.start();
    }

    public void start() {
        try (SocketChannel socketChannel = SocketChannel.open();
             Scanner scanner = new Scanner(System.in)) {

            buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
            socketChannel.connect(new InetSocketAddress(HOST, port));

            System.out.println("Successfully connected to the server.");

            while (true) {
                System.out.print("Enter command: ");

                String command = scanner.nextLine();

                writeServerOutput(socketChannel, command);

                if (command.equals("logout")) {

                    break;
                }

                try {
                    handleServerResponse(getServerInput(socketChannel));
                } catch (InvalidServerResponseException e) {
                    System.out.println(e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("A problem with the network communication occurred", e);
        }
    }

    private String getServerInput(SocketChannel clientChannel) throws IOException {
        buffer.clear();

        int readBytes = clientChannel.read(buffer);
        if (readBytes < 0) {
            clientChannel.close();
            return null;
        }

        buffer.flip();

        byte[] clientInputBytes = new byte[buffer.remaining()];
        buffer.get(clientInputBytes);

        return new String(clientInputBytes, StandardCharsets.UTF_8);
    }

    private void writeServerOutput(SocketChannel clientChannel, String output) throws IOException {
        buffer.clear();
        buffer.put(output.getBytes());
        buffer.flip();

        clientChannel.write(buffer);
    }

    private void handleServerResponse(String serverResponseJsonFormat) throws InvalidServerResponseException {
        ServerResponse serverResponse = ServerResponse.convertFromJson(serverResponseJsonFormat);

        if (serverResponse == null) {
            throw new InvalidServerResponseException();
        }

        System.out.println("Server response: ");
        System.out.println(serverResponse.getResponseStatus());
        System.out.println(serverResponse.getMessage());
    }

}
