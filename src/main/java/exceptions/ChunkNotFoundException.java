package exceptions;

public class ChunkNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ChunkNotFoundException() {
	}

	public ChunkNotFoundException(String message) {
		super(message);
	}

	public ChunkNotFoundException(Throwable cause) {
		super(cause);
	}

	public ChunkNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public ChunkNotFoundException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
