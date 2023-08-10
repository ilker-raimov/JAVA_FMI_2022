package bg.sofia.uni.fmi.mjt.todolist.exceptions;

public class IllegalTaskNameException extends Throwable {
    public static final String DEFAULT_MESSAGE_FORMAT = "Task name cannot be equal to the collaboration name - %s";

    public IllegalTaskNameException(String message) {
        super(message);
    }

    public IllegalTaskNameException(String message, Throwable cause) {
        super(message, cause);
    }
}
