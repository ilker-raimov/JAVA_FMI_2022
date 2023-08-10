package bg.sofia.uni.fmi.mjt.todolist.exceptions;

public class NoSuchLabelException extends Throwable {
    public static final String DEFAULT_MESSAGE_FORMAT = "User %1$s does not not have label named %2$s";

    public NoSuchLabelException(String message) {
        super(message);
    }

    public NoSuchLabelException(String message, Throwable cause) {
        super(message, cause);
    }
}
