package com.epam.esm.service.validation;

import org.springframework.util.StringUtils;

public class Util {
	public static String removeExtraSpaces(String source) {
		if(source != null) {
			return source.replaceAll("\\s+", " ").trim();
		}
		return null;
	}

	public static boolean checkLength(String source, int minLength, int maxLength) {
		if (!StringUtils.hasLength(source)) {
			return false;
		}
		String testedSource = removeExtraSpaces(source);
		return !testedSource.isBlank() && testedSource.length() >= minLength && testedSource.length() <= maxLength;
	}

}
