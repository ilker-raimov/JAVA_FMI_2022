package bg.sofia.uni.fmi.mjt.todolist.exceptions;

public class NoSuchArgumentException extends Throwable {
    public static final String DEFAULT_MESSAGE_FORMAT = "No such argument found: %s";

    public NoSuchArgumentException(String message) {
        super(message);
    }

    public NoSuchArgumentException(String message, Throwable cause) {
        super(message, cause);
    }
}
