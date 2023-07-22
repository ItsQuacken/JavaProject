package hr.java.projektnizadatak.shared.exceptions;

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
