package co.com.infrastructure.ports.file;

public class WritingException extends RuntimeException {

    public WritingException(String message) {
        super(message);
    }

    public WritingException(Throwable cause) {
        super(cause);
    }

    public WritingException(String message, Throwable cause) {
        super(message, cause);
    }
}
