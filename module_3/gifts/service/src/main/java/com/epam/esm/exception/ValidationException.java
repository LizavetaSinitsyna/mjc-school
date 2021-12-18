package com.epam.esm.exception;

import java.util.Map;

public class ValidationException extends GeneralException {

	private static final long serialVersionUID = 1L;

	public ValidationException(String invalidResource, ErrorCode errorCode) {
		super(invalidResource, errorCode);
	}

	public ValidationException(Map<ErrorCode, String> errors, ErrorCode generalErrorCode) {
		super(errors, generalErrorCode);
	}

}
