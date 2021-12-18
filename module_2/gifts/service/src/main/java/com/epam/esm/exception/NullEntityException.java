package com.epam.esm.exception;

public class NullEntityException extends GeneralException {

	private static final long serialVersionUID = 1L;

	public NullEntityException(String invalidResource, ErrorCode errorCode) {
		super(invalidResource, errorCode);
	}

}
