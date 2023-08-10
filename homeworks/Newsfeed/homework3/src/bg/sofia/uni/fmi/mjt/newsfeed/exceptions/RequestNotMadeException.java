package bg.sofia.uni.fmi.mjt.newsfeed.exceptions;

public class RequestNotMadeException extends Throwable {
    public RequestNotMadeException(String message) {
        super(message);
    }

    public RequestNotMadeException(String message, Throwable cause) {
        super(message, cause);
    }
}
