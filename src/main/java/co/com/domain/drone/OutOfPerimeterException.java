package co.com.domain.drone;

public class OutOfPerimeterException extends RuntimeException {

    public OutOfPerimeterException(String message) {
        super(message);
    }

    public OutOfPerimeterException(String message, Throwable cause) {
        super(message, cause);
    }
}