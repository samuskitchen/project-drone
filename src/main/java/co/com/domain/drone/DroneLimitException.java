package co.com.domain.drone;

public class DroneLimitException extends RuntimeException {

    public DroneLimitException(String message) {
        super(message);
    }

    public DroneLimitException(String message, Throwable cause) {
        super(message, cause);
    }
}