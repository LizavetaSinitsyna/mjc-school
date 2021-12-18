package com.epam.esm.exception;

public class NotFoundException extends GeneralException {

	private static final long serialVersionUID = 1L;

	public NotFoundException(String invalidResource, ErrorCode errorCode) {
		super(invalidResource, errorCode);
	}
}
