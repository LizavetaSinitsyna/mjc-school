package com.epam.esm.exception;

public class NotFoundException extends GeneralException {

	private static final long serialVersionUID = 1L;

	public NotFoundException() {
		super();
	}

	public NotFoundException(String invalidResource, ErrorCode errorCode) {
		super(invalidResource, errorCode);
	}

	public NotFoundException(String message) {
		super(message);
	}

	public NotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public NotFoundException(Throwable cause) {
		super(cause);
	}
}
