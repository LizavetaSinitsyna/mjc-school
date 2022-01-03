package com.epam.esm.exception;

import java.util.Map;

public class GeneralException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	private String invalidResource;
	private ErrorCode generalErrorCode;
	private Map<ErrorCode, String> errors;

	public GeneralException(String invalidResource, ErrorCode generalErrorCode) {
		this.invalidResource = invalidResource;
		this.generalErrorCode = generalErrorCode;
	}

	public GeneralException(Map<ErrorCode, String> errors, ErrorCode generalErrorCode) {
		this.errors = errors;
		this.generalErrorCode = generalErrorCode;
	}

	public String getInvalidResource() {
		return invalidResource;
	}

	public ErrorCode getGeneralErrorCode() {
		return generalErrorCode;
	}

	public Map<ErrorCode, String> getErrors() {
		return errors;
	}
}
