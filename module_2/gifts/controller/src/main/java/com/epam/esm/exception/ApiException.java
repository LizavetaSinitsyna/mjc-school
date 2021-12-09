package com.epam.esm.exception;

import java.util.Arrays;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApiException {

	private String errorMessage;
	private String errorCode;
	private List<String> causedErrors;

	public ApiException(String errorMessage, String errorCode, String error) {
		this.errorMessage = errorMessage;
		this.errorCode = errorCode;
		this.causedErrors = Arrays.asList(error);
	}

}
