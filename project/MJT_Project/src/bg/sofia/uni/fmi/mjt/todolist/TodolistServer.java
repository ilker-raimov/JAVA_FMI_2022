package bg.sofia.uni.fmi.mjt.todolist;

import bg.sofia.uni.fmi.mjt.todolist.command.CommandExecutor;
import bg.sofia.uni.fmi.mjt.todolist.command.CommandParser;
import bg.sofia.uni.fmi.mjt.todolist.command.CommandType;
import bg.sofia.uni.fmi.mjt.todolist.exceptions.InvalidArgumentCountException;
import bg.sofia.uni.fmi.mjt.todolist.exceptions.InvalidCommandSyntaxException;
import bg.sofia.uni.fmi.mjt.todolist.exceptions.NoSuchArgumentException;
import bg.sofia.uni.fmi.mjt.todolist.exceptions.RestrictedPermissionCommandException;
import bg.sofia.uni.fmi.mjt.todolist.exceptions.UnknownCommandException;
import bg.sofia.uni.fmi.mjt.todolist.response.ResponseStatus;
import bg.sofia.uni.fmi.mjt.todolist.response.ServerResponse;
import bg.sofia.uni.fmi.mjt.todolist.response.ServerResponseBuilder;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TodolistServer extends Thread {
    private static final int BUFFER_SIZE = 1024;
    private static final String HOST = "localhost";
    private static final String LOGOUT = "logout";
    private final int port;
    private boolean isServerWorking;
    private final ByteBuffer buffer;
    private Selector selector;

    private final CommandExecutor commandExecutor;
    private final ServerStorage serverStorage;
    private final Map<SelectionKey, String> ipToUserMap;
    private static final String DEFAULT_USERNAME = null;
    private static TodolistServer instance;

    public static void main(String[] args) {
        TodolistServer server = TodolistServer.getInstance(16868);
        server.start();
    }

    private TodolistServer(int port) {
        this.port = port;
        this.ipToUserMap = new HashMap<>();
        this.serverStorage = new ServerStorage();
        this.buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
        this.commandExecutor = new CommandExecutor(serverStorage);
    }

    public static TodolistServer getInstance(int port) {
        if (instance == null) {
            return instance = new TodolistServer(port);
        }

        return instance;
    }

    public void start() {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            SelectionKey key = null;

            selector = Selector.open();

            System.out.println("Server stared");

            configureServerSocketChannel(serverSocketChannel, selector);

            isServerWorking = true;

            while (isServerWorking) {
                try {
                    int readyChannels = selector.select();

                    if (readyChannels == 0) {

                        continue;
                    }

                    Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();

                    while (keyIterator.hasNext()) {

                        key = keyIterator.next();

                        if (key.isReadable()) {

                            SocketChannel clientChannel = (SocketChannel) key.channel();

                            String clientInput = null;

                            try {
                                clientInput = getClientInput(clientChannel);
                            } catch (IOException e) {
                                clientChannel.close();
                            }

                            if (!ipToUserMap.containsKey(key)) {
                                ipToUserMap.put(key, DEFAULT_USERNAME);
                            }

                            if (clientInput == null) {

                                continue;
                            }

                            if (clientInput.equals(LOGOUT)) {
                                clientChannel.close();

                                ipToUserMap.remove(key);

                                continue;
                            }

                            ServerResponseBuilder serverResponseBuilder;

                            try {
                                serverResponseBuilder = commandExecutor.execute(CommandParser
                                        .parseCommand(ipToUserMap.get(key), clientInput));

                            } catch (UnknownCommandException | InvalidArgumentCountException | NoSuchArgumentException |
                                     RestrictedPermissionCommandException | InvalidCommandSyntaxException e) {

                                serverResponseBuilder = new ServerResponseBuilder()
                                        .setResponseStatus(ResponseStatus.ERROR)
                                        .setClientUsername(ipToUserMap.get(key))
                                        .setMessage(e.getMessage());
                            }

                            ServerResponse serverResponse = serverResponseBuilder.build();

                            if (serverResponse.getCommandType() == CommandType.LOGIN &&
                                    serverResponse.getResponseStatus() == ResponseStatus.OK) {
                                ipToUserMap.put(key, serverResponse.getClientUsername());
                            }

                            writeClientOutput(clientChannel, serverResponse.convertToJson());

                        } else if (key.isAcceptable()) {

                            accept(selector, key);
                        }

                        keyIterator.remove();
                    }
                } catch (IOException e) {
                    selector.selectedKeys().clear();

                    if (key != null) {
                        ipToUserMap.remove(key);
                    }

                    System.out.println("Error occurred while processing client request: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException("failed to start server", e);
        }
    }

    public void stopServer() {
        this.isServerWorking = false;

        if (selector != null && selector.isOpen()) {
            selector.wakeup();
        }
    }

    private void configureServerSocketChannel(ServerSocketChannel channel, Selector selector) throws IOException {
        channel.bind(new InetSocketAddress(HOST, this.port));
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_ACCEPT);
    }

    private String getClientInput(SocketChannel clientChannel) throws IOException {
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

    private void writeClientOutput(SocketChannel clientChannel, String output) throws IOException {
        buffer.clear();
        buffer.put(output.getBytes());
        buffer.flip();

        clientChannel.write(buffer);
    }

    private void accept(Selector selector, SelectionKey key) throws IOException {
        ServerSocketChannel sockChannel = (ServerSocketChannel) key.channel(); //don't add try with res
        SocketChannel accept = sockChannel.accept();

        accept.configureBlocking(false);
        accept.register(selector, SelectionKey.OP_READ);
    }
}
