package com.epam.esm.service.validation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import com.epam.esm.exception.ErrorCode;
import com.epam.esm.exception.NullEntityException;

public class Util {
	public static final String DELIMITER = " = ";

	public static String removeExtraSpaces(String source) {
		if (source != null) {
			return source.trim().replaceAll("\\s+", " ");
		}
		return null;
	}

	public static MultiValueMap<String, String> mapToLowerCase(MultiValueMap<String, String> params) {
		checkNull(params);
		MultiValueMap<String, String> paramsInLowerCase = new LinkedMultiValueMap<>();
		for (Map.Entry<String, List<String>> entry : params.entrySet()) {
			paramsInLowerCase.put(entry.getKey().toLowerCase(), entry.getValue().stream().map(String::toLowerCase)
					.collect(Collectors.toCollection(ArrayList::new)));
		}
		return paramsInLowerCase;
	}

	public static Map<String, String> mapToLowerCase(Map<String, String> params) {
		checkNull(params);
		Map<String, String> paramsInLowerCase = new HashMap<>();
		for (Map.Entry<String, String> entry : params.entrySet()) {
			paramsInLowerCase.put(entry.getKey().toLowerCase(), entry.getValue().toLowerCase());
		}
		return paramsInLowerCase;
	}

	public static boolean checkLength(String source, int minLength, int maxLength) {
		if (!StringUtils.hasLength(source)) {
			return false;
		}
		String testedSource = removeExtraSpaces(source);
		return !testedSource.isBlank() && testedSource.length() >= minLength && testedSource.length() <= maxLength;
	}

	public static void checkNull(Object object) {
		if (object == null) {
			throw new NullEntityException("object = " + object, ErrorCode.NULL_PASSED_PARAMETER);
		}
	}

}
