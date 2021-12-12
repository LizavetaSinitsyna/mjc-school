package com.epam.esm.exception;

import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ApiRootCausesException extends ApiException {
	private List<String> causedErrors;

	public ApiRootCausesException(String errorMessage, String errorCode, String error) {
		super(errorMessage, errorCode);
		this.causedErrors = Arrays.asList(error);
	}

	public ApiRootCausesException(String errorMessage, String errorCode, List<String> causedErrors) {
		super(errorMessage, errorCode);
		this.causedErrors = causedErrors;
	}
}
