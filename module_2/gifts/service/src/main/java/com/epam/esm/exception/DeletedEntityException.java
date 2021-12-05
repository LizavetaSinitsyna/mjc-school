package com.epam.esm.exception;

public class DeletedEntityException extends GeneralException {

	private static final long serialVersionUID = 1L;

	public DeletedEntityException() {

	}

	public DeletedEntityException(String invalidResource, CustomErrorCode errorCode) {
		super();
	}

	public DeletedEntityException(String message) {
		super(message);
	}

	public DeletedEntityException(String message, Throwable cause) {
		super(message, cause);
	}

	public DeletedEntityException(Throwable cause) {
		super(cause);
	}

}
