package bg.sofia.uni.fmi.mjt.todolist.exceptions;

public class InvalidUsernameException extends Throwable {
    private static final String DEFAULT_MESSAGE = "Username must not be empty or blank";

    public InvalidUsernameException() {
        this(DEFAULT_MESSAGE);
    }

    public InvalidUsernameException(String message) {
        super(message);
    }

    public InvalidUsernameException(String message, Throwable cause) {
        super(message, cause);
    }
}
