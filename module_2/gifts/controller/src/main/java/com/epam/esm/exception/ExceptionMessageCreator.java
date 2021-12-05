package com.epam.esm.exception;

import java.util.Locale;
import java.util.ResourceBundle;

import org.springframework.stereotype.Component;

@Component
public class ExceptionMessageCreator {
	private static final String resourceBundleBasename = "exception";

	public String createMessage(CustomErrorCode errorCode, Locale locale, String invalidResource) {
		ResourceBundle resourceBundle = ResourceBundle.getBundle(resourceBundleBasename, locale);
		StringBuilder errorMessage = new StringBuilder();
		errorMessage.append(resourceBundle.getString(errorCode.getKey()));
		if (invalidResource != null) {
			errorMessage.append(" (");
			errorMessage.append(invalidResource);
			errorMessage.append(")");
		}
		return errorMessage.toString();
	}
}
