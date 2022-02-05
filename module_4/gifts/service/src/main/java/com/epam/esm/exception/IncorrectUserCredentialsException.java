package com.epam.esm.exception;

public class IncorrectUserCredentialsException extends GeneralException {
	private static final long serialVersionUID = 1L;

	public IncorrectUserCredentialsException(ErrorCode errorCode) {
		super(errorCode);
	}
}
