package com.epam.esm.exception;

public class ValidationException extends GeneralException {

	private static final long serialVersionUID = 1L;

	public ValidationException() {
		super();
	}

	public ValidationException(String invalidResource, CustomErrorCode errorCode) {
		super(invalidResource, errorCode);
	}

	public ValidationException(String message) {
		super(message);
	}

	public ValidationException(String message, Throwable cause) {
		super(message, cause);
	}

	public ValidationException(Throwable cause) {
		super(cause);
	}

}
