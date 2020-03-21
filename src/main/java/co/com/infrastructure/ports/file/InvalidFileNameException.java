package co.com.infrastructure.ports.file;

public class InvalidFileNameException extends RuntimeException {

    public InvalidFileNameException(String message) {
        super(message);
    }

    public InvalidFileNameException(Throwable cause) {
        super(cause);
    }

    public InvalidFileNameException(String message, Throwable cause) {
        super(message, cause);
    }
}