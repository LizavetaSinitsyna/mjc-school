package com.epam.esm.exception;

public class NullEntityException extends GeneralException {

	private static final long serialVersionUID = 1L;

	public NullEntityException() {
		super();
	}

	public NullEntityException(String invalidResource, ErrorCode errorCode) {
		super(invalidResource, errorCode);
	}
	
	public NullEntityException(String message) {
		super(message);
	}

	public NullEntityException(String message, Throwable cause) {
		super(message, cause);
	}

	public NullEntityException(Throwable cause) {
		super(cause);
	}

}
