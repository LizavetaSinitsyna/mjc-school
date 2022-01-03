package com.epam.esm.service.validation;

import com.epam.esm.dto.TagDto;
import com.epam.esm.exception.ErrorCode;
import com.epam.esm.repository.model.EntityConstant;
import com.epam.esm.service.ServiceConstant;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

/**
 * Contains methods for tag validation.
 *
 */
@Component
public class TagValidation {

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
		ValidationUtil.checkNull(tagDto, EntityConstant.TAG);
		Map<ErrorCode, String> errors = new HashMap<>();
		if (!ValidationUtil.checkLength(tagDto.getName(), ServiceConstant.TAG_MIN_NAME_LENGTH,
				ServiceConstant.TAG_MAX_NAME_LENGTH)) {
			errors.put(ErrorCode.INVALID_TAG_NAME,
					EntityConstant.NAME + ValidationUtil.ERROR_RESOURCE_DELIMITER + tagDto.getName());
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
		ValidationUtil.checkNull(params, EntityConstant.PARAMS);
		MultiValueMap<String, String> paramsInLowerCase = ValidationUtil.mapToLowerCase(params);
		Map<ErrorCode, String> errors = new HashMap<>();
		if (!ServiceConstant.GENERAL_POSSIBLE_READ_PARAMS.containsAll(paramsInLowerCase.keySet())) {
			errors.put(ErrorCode.INVALID_TAG_READ_PARAM,
					EntityConstant.PARAMS + ValidationUtil.ERROR_RESOURCE_DELIMITER + params);
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
