package com.epam.esm.service.validation;

import com.epam.esm.dto.UserDto;
import com.epam.esm.exception.ErrorCode;
import com.epam.esm.repository.model.EntityConstant;
import com.epam.esm.service.ServiceConstant;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

/**
 * Contains methods for user validation.
 *
 */
@Component
public class UserValidation {

	public UserValidation() {

	}

	/**
	 * Validates parameters for users reading.
	 * 
	 * @param paramsInLowerCase the parameters for users reading
	 * @return {@code Map} of {@code ErrorCode} as key and invalid resource as a
	 *         value for invalid parameters. If all parameters are valid returns
	 *         empty map
	 */
	public Map<ErrorCode, String> validateReadParams(MultiValueMap<String, String> params) {
		ValidationUtil.checkNull(params, EntityConstant.PARAMS);
		MultiValueMap<String, String> paramsInLowerCase = ValidationUtil.mapToLowerCase(params);
		Map<ErrorCode, String> errors = new HashMap<>();
		if (!ServiceConstant.GENERAL_POSSIBLE_READ_PARAMS.containsAll(paramsInLowerCase.keySet())) {
			errors.put(ErrorCode.INVALID_USER_READ_PARAM,
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

	/**
	 * Validates all user fields.
	 * 
	 * @param userDto the user to be validated
	 * @return {@code Map} of {@code ErrorCode} as key and invalid resource as a
	 *         value for invalid fields. If all fields are valid returns empty map
	 */
	public Map<ErrorCode, String> validateAllUserFields(UserDto userDto) {
		ValidationUtil.checkNull(userDto, EntityConstant.USER);
		Map<ErrorCode, String> errors = new HashMap<>();
		if (!ValidationUtil.checkLength(userDto.getLogin(), ServiceConstant.USER_MIN_NAME_LENGTH,
				ServiceConstant.USER_MAX_NAME_LENGTH)) {
			errors.put(ErrorCode.INVALID_USER_NAME,
					EntityConstant.NAME + ValidationUtil.ERROR_RESOURCE_DELIMITER + userDto.getLogin());
		}
		return errors;
	}
}
