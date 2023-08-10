package bg.sofia.uni.fmi.mjt.newsfeed.exceptions;

public class IllegalRequestArgumentsException extends Throwable {
    public IllegalRequestArgumentsException(String message) {
        super(message);
    }

    public IllegalRequestArgumentsException(String message, Throwable cause) {
        super(message, cause);
    }
}
