package bg.sofia.uni.fmi.mjt.todolist.exceptions;

public class NoSuchTaskException extends Throwable {
    public static final String DEFAULT_MESSAGE_FORMAT1 = "Task named %1$s does not exist";
    public static final String DEFAULT_MESSAGE_FORMAT2 = "Task named %1$s on %2$s does not exist";

    public NoSuchTaskException(String message) {
        super(message);
    }

    public NoSuchTaskException(String message, Throwable cause) {
        super(message, cause);
    }
}
