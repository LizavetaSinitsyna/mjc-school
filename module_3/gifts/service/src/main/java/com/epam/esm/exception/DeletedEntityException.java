package com.epam.esm.exception;

public class DeletedEntityException extends GeneralException {

	private static final long serialVersionUID = 1L;

	public DeletedEntityException(String invalidResource, ErrorCode errorCode) {
		super(invalidResource, errorCode);
	}

}
