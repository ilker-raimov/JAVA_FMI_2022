package bg.sofia.uni.fmi.mjt.escaperoom.exception;

public class PlatformCapacityExceededException extends RuntimeException {
    public PlatformCapacityExceededException() {
        super("Platform capacity exceeded");
    }
}
