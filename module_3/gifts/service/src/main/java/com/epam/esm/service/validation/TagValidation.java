package com.epam.esm.service.validation;

import com.epam.esm.dto.TagDto;
import com.epam.esm.exception.ErrorCode;
import com.epam.esm.repository.query_builder.EntityConstant;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

@Component
public class TagValidation {
	private static final int MIN_NAME_LENGTH = 2;
	private static final int MAX_NAME_LENGTH = 25;
	private static final Set<String> POSSIBLE_READ_PARAMS = new HashSet<String>(Arrays.asList("page", "limit"));
	private static final int DEFAULT_PAGE_NUMBER = 1;
	private static final int OFFSET = 10;

	public TagValidation() {

	}

	/**
	 * Validates all tag fields.
	 * 
	 * @param tagDto the tag to be validated
	 * @return {@code Map} of {@code ErrorCode} as key and invalid resource as a
	 *         value for invalid fields. If all fields are valid returns empty map
	 */

	public Map<ErrorCode, String> validateAllTagFields(TagDto tagDto) {
		Util.checkNull(tagDto);
		Map<ErrorCode, String> errors = new HashMap<>();
		if (!Util.checkLength(tagDto.getName(), MIN_NAME_LENGTH, MAX_NAME_LENGTH)) {
			errors.put(ErrorCode.INVALID_TAG_NAME, EntityConstant.NAME + Util.DELIMITER + tagDto.getName());
		}
		return errors;
	}

	/**
	 * Validates parameters for tags reading.
	 * 
	 * @param paramsInLowerCase the parameters for tags reading
	 * @return {@code Map} of {@code ErrorCode} as key and invalid resource as a
	 *         value for invalid parameters. If all parameters are valid returns
	 *         empty map
	 */
	public Map<ErrorCode, String> validateReadParams(MultiValueMap<String, String> paramsInLowerCase) {
		Util.checkNull(paramsInLowerCase);
		Map<ErrorCode, String> errors = new HashMap<>();
		if (!POSSIBLE_READ_PARAMS.containsAll(paramsInLowerCase.keySet())) {
			errors.put(ErrorCode.INVALID_TAG_READ_PARAM, EntityConstant.PARAMS + Util.DELIMITER + paramsInLowerCase);
		}

		if (paramsInLowerCase.containsKey(EntityConstant.PAGE)) {
			int page = DEFAULT_PAGE_NUMBER;
			String initialPage = paramsInLowerCase.get(EntityConstant.PAGE).get(0);
			try {
				page = Integer.parseInt(initialPage);
			} catch (NumberFormatException e) {
				errors.put(ErrorCode.INVALID_PAGE_FORMAT, EntityConstant.PAGE + Util.DELIMITER + initialPage);
			}
			if (page <= 0) {
				errors.put(ErrorCode.NEGATIVE_PAGE_NUMBER, EntityConstant.PAGE + Util.DELIMITER + page);
			}
		} else {
			paramsInLowerCase.put(EntityConstant.PAGE, Arrays.asList(Integer.toString(DEFAULT_PAGE_NUMBER)));
		}

		if (paramsInLowerCase.containsKey(EntityConstant.LIMIT)) {
			int limit = OFFSET;
			String initialOffset = paramsInLowerCase.get(EntityConstant.LIMIT).get(0);
			try {
				limit = Integer.parseInt(initialOffset);
			} catch (NumberFormatException e) {
				errors.put(ErrorCode.INVALID_OFFSET_FORMAT, EntityConstant.LIMIT + Util.DELIMITER + initialOffset);
			}
			if (limit <= 0) {
				errors.put(ErrorCode.NEGATIVE_OFFSET, EntityConstant.LIMIT + Util.DELIMITER + limit);
			}
		} else {
			paramsInLowerCase.put(EntityConstant.LIMIT, Arrays.asList(Integer.toString(OFFSET)));
		}

		return errors;

	}

}
