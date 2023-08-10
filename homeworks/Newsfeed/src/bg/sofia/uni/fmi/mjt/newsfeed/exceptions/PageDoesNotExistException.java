package bg.sofia.uni.fmi.mjt.newsfeed.exceptions;

public class PageDoesNotExistException extends Throwable {
    public PageDoesNotExistException(String message) {
        super(message);
    }

    public PageDoesNotExistException(String message, Throwable cause) {
        super(message, cause);
    }
}
