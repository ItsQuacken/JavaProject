package hr.java.projekt.exceptions;

public class FxmlLoadingException extends RuntimeException {
	public FxmlLoadingException() {
	}

	public FxmlLoadingException(String message) {
		super(message);
	}

	public FxmlLoadingException(String message, Throwable cause) {
		super(message, cause);
	}

	public FxmlLoadingException(Throwable cause) {
		super(cause);
	}
}
