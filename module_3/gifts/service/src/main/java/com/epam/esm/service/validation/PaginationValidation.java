package com.epam.esm.service.validation;

import java.util.HashMap;
import java.util.Map;

import com.epam.esm.exception.ErrorCode;
import com.epam.esm.repository.model.EntityConstant;

public class PaginationValidation {
	private PaginationValidation() {

	}

	public static Map<ErrorCode, String> validateLimit(String initialLimit) {
		Map<ErrorCode, String> errors = new HashMap<>();
		int limit = -1;
		try {
			limit = Integer.parseInt(initialLimit);
		} catch (NumberFormatException e) {
			errors.put(ErrorCode.INVALID_LIMIT_FORMAT, EntityConstant.LIMIT + Util.ERROR_RESOURCE_DELIMITER + initialLimit);
		}
		if (limit <= 0) {
			errors.put(ErrorCode.NEGATIVE_LIMIT, EntityConstant.LIMIT + Util.ERROR_RESOURCE_DELIMITER + limit);
		}

		return errors;

	}

	public static Map<ErrorCode, String> validateOffset(String initialOffset) {
		Map<ErrorCode, String> errors = new HashMap<>();
		int offset = -1;
		try {
			offset = Integer.parseInt(initialOffset);
		} catch (NumberFormatException e) {
			errors.put(ErrorCode.INVALID_OFFSET_FORMAT, EntityConstant.OFFSET + Util.ERROR_RESOURCE_DELIMITER + initialOffset);
		}
		if (offset < 0) {
			errors.put(ErrorCode.NEGATIVE_OFFSET, EntityConstant.OFFSET + Util.ERROR_RESOURCE_DELIMITER + offset);
		}
		return errors;

	}
}
