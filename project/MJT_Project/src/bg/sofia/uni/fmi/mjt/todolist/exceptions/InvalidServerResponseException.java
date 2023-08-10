package bg.sofia.uni.fmi.mjt.todolist.exceptions;

public class InvalidServerResponseException extends Throwable {
    private static final String DEFAULT_MESSAGE = "Invalid server response format";

    public InvalidServerResponseException() {
        this(DEFAULT_MESSAGE);
    }

    public InvalidServerResponseException(String message) {
        super(message);
    }

    public InvalidServerResponseException(String message, Throwable cause) {
        super(message, cause);
    }
}
