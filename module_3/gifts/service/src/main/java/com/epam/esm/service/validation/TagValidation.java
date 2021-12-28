package com.epam.esm.service.validation;

import com.epam.esm.dto.TagDto;
import com.epam.esm.exception.ErrorCode;
import com.epam.esm.repository.model.EntityConstant;

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
	private static final Set<String> POSSIBLE_READ_PARAMS = new HashSet<String>(Arrays.asList("offset", "limit"));

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
		Util.checkNull(tagDto, EntityConstant.TAG);
		Map<ErrorCode, String> errors = new HashMap<>();
		if (!Util.checkLength(tagDto.getName(), MIN_NAME_LENGTH, MAX_NAME_LENGTH)) {
			errors.put(ErrorCode.INVALID_TAG_NAME, EntityConstant.NAME + Util.ERROR_RESOURCE_DELIMITER + tagDto.getName());
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
	public Map<ErrorCode, String> validateReadParams(MultiValueMap<String, String> params) {
		Util.checkNull(params, EntityConstant.PARAMS);
		MultiValueMap<String, String> paramsInLowerCase = Util.mapToLowerCase(params);
		Map<ErrorCode, String> errors = new HashMap<>();
		if (!POSSIBLE_READ_PARAMS.containsAll(paramsInLowerCase.keySet())) {
			errors.put(ErrorCode.INVALID_TAG_READ_PARAM, EntityConstant.PARAMS + Util.ERROR_RESOURCE_DELIMITER + params);
		}

		if (paramsInLowerCase.containsKey(EntityConstant.OFFSET)) {
			errors.putAll(PaginationValidation.validateOffset(paramsInLowerCase.get(EntityConstant.OFFSET).get(0)));
		}

		if (paramsInLowerCase.containsKey(EntityConstant.LIMIT)) {
			errors.putAll(PaginationValidation.validateLimit(paramsInLowerCase.get(EntityConstant.LIMIT).get(0)));
		}

		return errors;
	}
}
