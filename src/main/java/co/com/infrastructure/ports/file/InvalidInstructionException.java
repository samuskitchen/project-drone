package co.com.infrastructure.ports.file;

public class InvalidInstructionException extends RuntimeException {

    public InvalidInstructionException(String message) {
        super(message);
    }

    public InvalidInstructionException(Throwable cause) {
        super(cause);
    }

    public InvalidInstructionException(String message, Throwable cause) {
        super(message, cause);
    }
}