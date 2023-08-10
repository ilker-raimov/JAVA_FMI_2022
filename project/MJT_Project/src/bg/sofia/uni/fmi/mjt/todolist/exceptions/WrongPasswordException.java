package bg.sofia.uni.fmi.mjt.todolist.exceptions;

public class WrongPasswordException extends Throwable {
    public WrongPasswordException(String message) {
        super(message);
    }

    public WrongPasswordException(String message, Throwable cause) {
        super(message, cause);
    }
}
