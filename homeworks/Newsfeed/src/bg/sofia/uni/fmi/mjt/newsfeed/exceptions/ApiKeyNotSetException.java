package bg.sofia.uni.fmi.mjt.newsfeed.exceptions;

public class ApiKeyNotSetException extends Throwable {
    public ApiKeyNotSetException(String message) {
        super(message);
    }

    public ApiKeyNotSetException(String message, Throwable cause) {
        super(message, cause);
    }
}
