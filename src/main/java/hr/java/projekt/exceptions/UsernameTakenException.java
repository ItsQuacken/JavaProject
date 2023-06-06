package hr.java.projekt.exceptions;

public class UsernameTakenException extends Exception {
    public UsernameTakenException() {
    }

    public UsernameTakenException(String message) {
        super(message);
    }

    public UsernameTakenException(String message, Throwable cause) {
        super(message, cause);
    }

    public UsernameTakenException(Throwable cause) {
        super(cause);
    }
}
