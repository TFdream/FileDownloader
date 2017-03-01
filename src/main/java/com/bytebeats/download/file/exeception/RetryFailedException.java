package com.bytebeats.download.file.exeception;

public class RetryFailedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public RetryFailedException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public RetryFailedException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	public RetryFailedException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public RetryFailedException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public RetryFailedException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
