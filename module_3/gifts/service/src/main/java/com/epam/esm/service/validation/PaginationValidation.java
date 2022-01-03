package com.epam.esm.service.validation;

import java.util.HashMap;
import java.util.Map;

import com.epam.esm.exception.ErrorCode;
import com.epam.esm.repository.model.EntityConstant;

/**
 * Contains methods for pagination validation.
 *
 */
public class PaginationValidation {
	private PaginationValidation() {

	}

	/**
	 * Validates limit pagination parameter.
	 * 
	 * @param initialLimit the limit for validation
	 * @return {@code Map} of {@code ErrorCode} as key and invalid parameter as a
	 *         value for invalid limit. If limit is valid returns empty map
	 */

	public static Map<ErrorCode, String> validateLimit(String initialLimit) {
		Map<ErrorCode, String> errors = new HashMap<>();
		int limit = -1;
		try {
			limit = Integer.parseInt(initialLimit);
		} catch (NumberFormatException e) {
			errors.put(ErrorCode.INVALID_LIMIT_FORMAT,
					EntityConstant.LIMIT + ValidationUtil.ERROR_RESOURCE_DELIMITER + initialLimit);
		}
		if (limit <= 0) {
			errors.put(ErrorCode.NEGATIVE_LIMIT,
					EntityConstant.LIMIT + ValidationUtil.ERROR_RESOURCE_DELIMITER + limit);
		}
		return errors;
	}

	/**
	 * Validates offset pagination parameter.
	 * 
	 * @param initialOffset the offset for validation
	 * @return {@code Map} of {@code ErrorCode} as key and invalid parameter as a
	 *         value for invalid offset. If offset is valid returns empty map
	 */
	public static Map<ErrorCode, String> validateOffset(String initialOffset) {
		Map<ErrorCode, String> errors = new HashMap<>();
		int offset = -1;
		try {
			offset = Integer.parseInt(initialOffset);
		} catch (NumberFormatException e) {
			errors.put(ErrorCode.INVALID_OFFSET_FORMAT,
					EntityConstant.OFFSET + ValidationUtil.ERROR_RESOURCE_DELIMITER + initialOffset);
		}
		if (offset < 0) {
			errors.put(ErrorCode.NEGATIVE_OFFSET,
					EntityConstant.OFFSET + ValidationUtil.ERROR_RESOURCE_DELIMITER + offset);
		}
		return errors;
	}
}
