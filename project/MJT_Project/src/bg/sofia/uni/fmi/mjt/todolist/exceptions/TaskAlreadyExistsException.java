package bg.sofia.uni.fmi.mjt.todolist.exceptions;

public class TaskAlreadyExistsException extends Throwable {
    public TaskAlreadyExistsException(String message) {
        super(message);
    }

    public TaskAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
