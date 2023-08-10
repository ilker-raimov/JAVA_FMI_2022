package bg.sofia.uni.fmi.mjt.todolist.exceptions;

public class UserAlreadyAddedException extends Throwable {
    public UserAlreadyAddedException(String message) {
        super(message);
    }

    public UserAlreadyAddedException(String message, Throwable cause) {
        super(message, cause);
    }
}
