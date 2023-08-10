package bg.sofia.uni.fmi.mjt.todolist.exceptions;

public class InvalidPasswordException extends Throwable {
    private static final String DEFAULT_MESSAGE = "Password must be at least 8 characters and not blank";

    public InvalidPasswordException() {
        this(DEFAULT_MESSAGE);
    }

    public InvalidPasswordException(String message) {
        super(message);
    }

    public InvalidPasswordException(String message, Throwable cause) {
        super(message, cause);
    }
}
