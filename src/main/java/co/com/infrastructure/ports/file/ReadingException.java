package co.com.infrastructure.ports.file;

public class ReadingException extends RuntimeException {

    public ReadingException(String message) {
        super(message);
    }

    public ReadingException(Throwable cause) {
        super(cause);
    }

    public ReadingException(String message, Throwable cause) {
        super(message, cause);
    }
}
