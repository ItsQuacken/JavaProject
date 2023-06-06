package hr.java.projekt.exceptions;

public class ReadOrWriteErrorException extends RuntimeException {
	public ReadOrWriteErrorException() {
	}

	public ReadOrWriteErrorException(String message) {
		super(message);
	}

	public ReadOrWriteErrorException(String message, Throwable cause) {
		super(message, cause);
	}

	public ReadOrWriteErrorException(Throwable cause) {
		super(cause);
	}
}
