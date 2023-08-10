package bg.sofia.uni.fmi.mjt.todolist.exceptions;

public class CollaborationAlreadyExistsException extends Throwable {
    public static final String DEFAULT_MESSAGE_FORMAT = "Collaboration named %1$s owned by %2$s already exists";

    public CollaborationAlreadyExistsException(String message) {
        super(message);
    }

    public CollaborationAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
