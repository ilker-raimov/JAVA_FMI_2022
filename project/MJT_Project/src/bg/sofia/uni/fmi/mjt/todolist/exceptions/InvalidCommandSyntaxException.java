package bg.sofia.uni.fmi.mjt.todolist.exceptions;

public class InvalidCommandSyntaxException extends Throwable {
    public InvalidCommandSyntaxException(String message) {
        super(message);
    }

    InvalidCommandSyntaxException(String message, Throwable cause) {
        super(message, cause);
    }
}
