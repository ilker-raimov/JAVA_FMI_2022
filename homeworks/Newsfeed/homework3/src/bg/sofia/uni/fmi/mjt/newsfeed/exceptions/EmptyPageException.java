package bg.sofia.uni.fmi.mjt.newsfeed.exceptions;

public class EmptyPageException extends Throwable {
    public EmptyPageException(String message) {
        super(message);
    }

    public EmptyPageException(String message, Throwable cause) {
        super(message, cause);
    }
}
