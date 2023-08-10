package bg.sofia.uni.fmi.mjt.todolist.exceptions;

public class IOUnsuccessfulOperationException extends Throwable {
    public static final String DEFAULT_MESSAGE_FORMAT = "Unsuccessful IO operation: %s";

    public IOUnsuccessfulOperationException(String message) {
        super(message);
    }

    public IOUnsuccessfulOperationException(String message, Throwable cause) {
        super(message, cause);
    }

}
