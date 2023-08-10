package bg.sofia.uni.fmi.mjt.todolist.response;

import bg.sofia.uni.fmi.mjt.todolist.command.CommandType;
import com.google.gson.Gson;

public class ServerResponse {
    private final ResponseStatus responseStatus;
    private final CommandType commandType;
    private final String clientUsername;
    private final String message;
    private static final Gson parser = new Gson();

    public ServerResponse(ServerResponseBuilder serverResponseBuilder) {
        this.responseStatus = serverResponseBuilder.getResponseStatus();
        this.commandType = serverResponseBuilder.getCommandType();
        this.clientUsername = serverResponseBuilder.getClientUsername();
        this.message = serverResponseBuilder.getMessage();
    }

    public String convertToJson() {
        return parser.toJson(this, this.getClass());
    }

    public static ServerResponse convertFromJson(String serverResponseJsonFormat) {
        return parser.fromJson(serverResponseJsonFormat, ServerResponse.class);
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
