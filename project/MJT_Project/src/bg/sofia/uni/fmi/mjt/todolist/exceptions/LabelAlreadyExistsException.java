package bg.sofia.uni.fmi.mjt.todolist.exceptions;

public class LabelAlreadyExistsException extends Throwable {
    public static final String DEFAULT_MESSAGE_FORMAT = "Label named %1$s already exists for user %2$s";

    public LabelAlreadyExistsException(String message) {
        super(message);
    }

    public LabelAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
