package bg.sofia.uni.fmi.mjt.todolist.exceptions;

public class UnknownCommandException extends Throwable {
    public UnknownCommandException(String message) {
        super(message);
    }

    public UnknownCommandException(String message, Throwable cause) {
        super(message, cause);
    }
}
