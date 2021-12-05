package com.epam.esm.exception;

public class GeneralException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private String invalidResource;
	private CustomErrorCode errorCode;

	public GeneralException() {
		super();
	}

	public GeneralException(String invalidResource, CustomErrorCode errorCode) {
		super();
		this.invalidResource = invalidResource;
		this.errorCode = errorCode;
	}

	public GeneralException(String message) {
		super(message);
	}

	public GeneralException(String message, Throwable cause) {
		super(message, cause);
	}

	public GeneralException(Throwable cause) {
		super(cause);
	}

	public String getInvalidResource() {
		return invalidResource;
	}

	public CustomErrorCode getErrorCode() {
		return errorCode;
	}

}
