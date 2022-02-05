package com.epam.esm.exception;

public class InvalidJwtException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public InvalidJwtException(String message) {
		super(message);
	}
}
