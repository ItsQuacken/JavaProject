package hr.java.projekt.exceptions;

public class UnsupportedAlgorithmException extends RuntimeException {
    public UnsupportedAlgorithmException() {
    }

    public UnsupportedAlgorithmException(String message) {
        super(message);
    }

    public UnsupportedAlgorithmException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsupportedAlgorithmException(Throwable cause) {
        super(cause);
    }
}

