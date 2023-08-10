package bg.sofia.uni.fmi.mjt.todolist.exceptions;

public class RestrictedPermissionCommandException extends Throwable {
    public RestrictedPermissionCommandException(String message) {
        super(message);
    }

    public RestrictedPermissionCommandException(String message, Throwable cause) {
        super(message, cause);
    }
}
