package bg.sofia.uni.fmi.mjt.todolist.exceptions;

public class InvalidDateFormatException extends Throwable {
    public static final String DEFAULT_MESSAGE_FORMAT = "Invalid date format. Required: %s";

    public InvalidDateFormatException(String message) {
        super(message);
    }

    public InvalidDateFormatException(String message, Throwable cause) {
        super(message, cause);
    }
}
