package bg.sofia.uni.fmi.mjt.todolist.exceptions;

public class InvalidDateException extends Throwable {
    public static final String DEFAULT_MESSAGE = "Date cannot be in the past";

    public InvalidDateException() {
        this(DEFAULT_MESSAGE);
    }

    public InvalidDateException(String message) {
        super(message);
    }

    public InvalidDateException(String message, Throwable cause) {
        super(message, cause);
    }

}
