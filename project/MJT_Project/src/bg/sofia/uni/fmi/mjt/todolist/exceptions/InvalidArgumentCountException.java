package bg.sofia.uni.fmi.mjt.todolist.exceptions;

public class InvalidArgumentCountException extends Throwable {
    public InvalidArgumentCountException(String message) {
        super(message);
    }

    public InvalidArgumentCountException(String message, Throwable cause) {
        super(message, cause);
    }
}
