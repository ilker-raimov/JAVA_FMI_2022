package bg.sofia.uni.fmi.mjt.todolist.response;

import bg.sofia.uni.fmi.mjt.todolist.command.CommandType;

public class ServerResponseBuilder {
    private ResponseStatus responseStatus;
    private CommandType commandType;
    private String clientUsername;
    private String message;

    public ServerResponseBuilder() {
    }

    public ServerResponse build() {
        return new ServerResponse(this);
    }

    public ServerResponseBuilder setResponseStatus(ResponseStatus responseStatus) {
        this.responseStatus = responseStatus;

        return this;
    }

    public ServerResponseBuilder setCommandType(CommandType commandType) {
        this.commandType = commandType;

        return this;
    }

    public ServerResponseBuilder setClientUsername(String clientUsername) {
        this.clientUsername = clientUsername;

        return this;
    }

    public ServerResponseBuilder setMessage(String message) {
        this.message = message;

        return this;
    }

    public ResponseStatus getResponseStatus() {
        return responseStatus;
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public String getClientUsername() {
        return clientUsername;
    }

    public String getMessage() {
        return message;
    }
}
