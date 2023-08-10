package bg.sofia.uni.fmi.mjt.todolist.exceptions;

public class NoSuchCollaborationExistsException extends Throwable {
    public static final String DEFAULT_MESSAGE_FORMAT = "Collaboration named %1$s owned by %2$s does not exist";

    public NoSuchCollaborationExistsException(String message) {
        super(message);
    }

    public NoSuchCollaborationExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
