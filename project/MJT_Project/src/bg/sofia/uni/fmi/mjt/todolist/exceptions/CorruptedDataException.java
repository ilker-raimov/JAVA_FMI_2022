package bg.sofia.uni.fmi.mjt.todolist.exceptions;

public class CorruptedDataException extends Throwable {
    public CorruptedDataException(String message) {
        super(message);
    }

    public CorruptedDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
