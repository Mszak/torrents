package exceptions;

public class FileNotChunkedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public FileNotChunkedException() {
	}

	public FileNotChunkedException(String message) {
		super(message);
	}

	public FileNotChunkedException(Throwable cause) {
		super(cause);
	}

	public FileNotChunkedException(String message, Throwable cause) {
		super(message, cause);
	}

	public FileNotChunkedException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
