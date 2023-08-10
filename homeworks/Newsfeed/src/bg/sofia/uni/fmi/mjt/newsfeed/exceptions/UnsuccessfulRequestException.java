package bg.sofia.uni.fmi.mjt.newsfeed.exceptions;

public class UnsuccessfulRequestException extends Throwable {
    public UnsuccessfulRequestException(String message) {
        super(message);
    }

    public UnsuccessfulRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
