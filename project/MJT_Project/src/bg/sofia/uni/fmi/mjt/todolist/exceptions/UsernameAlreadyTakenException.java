package bg.sofia.uni.fmi.mjt.todolist.exceptions;

public class UsernameAlreadyTakenException extends Throwable {
    public UsernameAlreadyTakenException(String message) {
        super(message);
    }

    UsernameAlreadyTakenException(String message, Throwable cause) {
        super(message, cause);
    }
}
