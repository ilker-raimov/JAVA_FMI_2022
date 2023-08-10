package bg.sofia.uni.fmi.mjt.todolist.exceptions;

public class NoSuchUserException extends Throwable {
    public NoSuchUserException(String message) {
        super(message);
    }

    public NoSuchUserException(String message, Throwable cause) {
        super(message, cause);
    }
}
