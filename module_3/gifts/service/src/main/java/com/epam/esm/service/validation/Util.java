package com.epam.esm.service.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import com.epam.esm.exception.ErrorCode;
import com.epam.esm.exception.NullEntityException;

/**
 * Contains common methods which help in entity validation.
 *
 */
public class Util {
	public static final String ERROR_RESOURCE_DELIMITER = " = ";
	public static final String ERROR_RESOURCES_LIST_DELIMITER = ", ";

	private Util() {

	}

	/**
	 * Checks if the passed value is positive.
	 * 
	 * @param value the value to be checked
	 * @return {@code true} if the value is positive; {@code false} otherwise
	 */
	public static boolean isPositive(Long value) {
		return value != null && value > 0;
	}

	/**
	 * Removes extra leading, trailing spaces and extra spaces in the middle of the
	 * passed string.
	 * 
	 * @param source the string from which spaces should be removed
	 * @return passed string without extra spaces. If {@code source} is
	 *         {@code null}, then returns {@code null}
	 */
	public static String removeExtraSpaces(String source) {
		if (source != null) {
			return source.trim().replaceAll("\\s+", " ");
		}
		return null;
	}

	/**
	 * Turns keys and values of passed map to lower case.
	 * 
	 * @param params the map which should be in lower case
	 * @return passed map with all keys and values in lower case
	 * @throws NullEntityException if passed object is {@code null}
	 * @see #checkNull(Object)
	 */
	public static MultiValueMap<String, String> mapToLowerCase(MultiValueMap<String, String> params) {
		checkNull(params);
		MultiValueMap<String, String> paramsInLowerCase = new LinkedMultiValueMap<>();
		for (Map.Entry<String, List<String>> entry : params.entrySet()) {
			String key = entry.getKey().toLowerCase();
			if (paramsInLowerCase.containsKey(key)) {
				paramsInLowerCase.get(key).addAll(entry.getValue());
			} else {
				paramsInLowerCase.put(entry.getKey().toLowerCase(), entry.getValue().stream().map(String::toLowerCase)
						.collect(Collectors.toCollection(ArrayList::new)));
			}
		}
		return paramsInLowerCase;
	}

	/**
	 * Checks the length requirements to the passed string. Before performing
	 * checking it removes extra spaces.
	 * 
	 * @param source    the value to be checked
	 * @param minLength the required minimum length for passed string
	 * @param maxLength the required maximum length for passed string
	 * @return {@code true} if the length of passed {@code source} is between
	 *         {@code minLength} and {@code maxLength}; {@code false} otherwise
	 * @see #removeExtraSpaces(String)
	 */
	public static boolean checkLength(String source, int minLength, int maxLength) {
		if (!StringUtils.hasLength(source)) {
			return false;
		}
		String testedSource = removeExtraSpaces(source);
		return !testedSource.isBlank() && testedSource.length() >= minLength && testedSource.length() <= maxLength;
	}

	/**
	 * Check if passed object is null.
	 * 
	 * @param object the object to be checked
	 * @throws NullEntityException if passed object is {@code null}
	 */
	public static void checkNull(Object object) {
		if (object == null) {
			throw new NullEntityException("object" + ERROR_RESOURCE_DELIMITER + object, ErrorCode.NULL_PASSED_PARAMETER);
		}
	}

}
