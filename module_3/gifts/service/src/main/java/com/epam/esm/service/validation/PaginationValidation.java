package com.epam.esm.service.validation;

import java.util.HashMap;
import java.util.Map;

import com.epam.esm.exception.ErrorCode;
import com.epam.esm.repository.model.EntityConstant;
import com.epam.esm.service.ServiceConstant;

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
					ServiceConstant.LIMIT + ValidationUtil.ERROR_RESOURCE_DELIMITER + initialLimit);
		}
		if (errors.isEmpty() && limit < ServiceConstant.MIN_LIMIT_NUMBER) {
			errors.put(ErrorCode.NEGATIVE_LIMIT,
					ServiceConstant.LIMIT + ValidationUtil.ERROR_RESOURCE_DELIMITER + limit);
		} else if (limit > ServiceConstant.MAX_LIMIT) {
			errors.put(ErrorCode.TOO_LARGE_LIMIT,
					ServiceConstant.LIMIT + ValidationUtil.ERROR_RESOURCE_DELIMITER + limit);
		}
		return errors;
	}

	/**
	 * Validates offset pagination parameter.
	 * 
	 * @param initialPageNumber the offset for validation
	 * @return {@code Map} of {@code ErrorCode} as key and invalid parameter as a
	 *         value for invalid offset. If offset is valid returns empty map
	 */
	public static Map<ErrorCode, String> validateOffset(String initialPageNumber) {
		Map<ErrorCode, String> errors = new HashMap<>();
		int pageNumber = -1;
		try {
			pageNumber = Integer.parseInt(initialPageNumber);
		} catch (NumberFormatException e) {
			errors.put(ErrorCode.INVALID_OFFSET_FORMAT,
					ServiceConstant.OFFSET + ValidationUtil.ERROR_RESOURCE_DELIMITER + initialPageNumber);
		}
		if (errors.isEmpty() && pageNumber <  ServiceConstant.MIN_PAGE_NUMBER) {
			errors.put(ErrorCode.NEGATIVE_OFFSET,
					ServiceConstant.OFFSET + ValidationUtil.ERROR_RESOURCE_DELIMITER + pageNumber);
		}
		return errors;
	}
}
