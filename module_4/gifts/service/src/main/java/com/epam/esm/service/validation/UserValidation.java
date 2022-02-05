package com.epam.esm.service.validation;

import com.epam.esm.dto.UserDto;
import com.epam.esm.exception.ErrorCode;
import com.epam.esm.repository.model.EntityConstant;
import com.epam.esm.service.ServiceConstant;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

/**
 * Contains methods for user validation.
 *
 */
@Component
public class UserValidation {
	private static final String PASSWORD_REGEX = "^(?=.*?[A-ZА-Я])(?=.*?[a-zа-я])(?=.*?[0-9])(?=.*?[ !\\\"#\\$%&'\\(\\)\\*\\+,\\-\\./:;<=>\\?@\\[\\]\\^_`{|}~]).{8,25}$";
	private static final String USERNAME_REGEX = "^[a-zA-Z0-9а-яА-Я]([\\._-](?![\\._-])|[a-zA-Z0-9а-яА-Я]){3,18}[a-zA-Z0-9а-яА-Я]$";

	public UserValidation() {

	}

	/**
	 * Validates username.
	 * 
	 * @param username the username to validate.
	 * @return {@code true} if username is valid and {@code false} otherwise.
	 */
	public boolean validateUsername(String username) {
		Pattern pattern = Pattern.compile(USERNAME_REGEX,  Pattern.UNICODE_CHARACTER_CLASS);
		return !(username == null) && pattern.matcher(username).matches();

	}

	/**
	 * Validates password.
	 * 
	 * @param password the password to validate.
	 * @return {@code true} if password is valid and {@code false} otherwise.
	 */

	public boolean checkPassword(String password) {
		Pattern pattern = Pattern.compile(PASSWORD_REGEX);
		return !(password == null) && pattern.matcher(password).matches();
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
		ValidationUtil.checkNull(params, ServiceConstant.PARAMS);
		MultiValueMap<String, String> paramsInLowerCase = ValidationUtil.mapToLowerCase(params);
		Map<ErrorCode, String> errors = new HashMap<>();
		if (!ServiceConstant.GENERAL_POSSIBLE_READ_PARAMS.containsAll(paramsInLowerCase.keySet())) {
			errors.put(ErrorCode.INVALID_USER_READ_PARAM,
					ServiceConstant.PARAMS + ValidationUtil.ERROR_RESOURCE_DELIMITER + params);
		}

		if (paramsInLowerCase.containsKey(ServiceConstant.OFFSET)) {
			errors.putAll(PaginationValidation.validateOffset(paramsInLowerCase.get(ServiceConstant.OFFSET).get(0)));
		}

		if (paramsInLowerCase.containsKey(ServiceConstant.LIMIT)) {
			errors.putAll(PaginationValidation.validateLimit(paramsInLowerCase.get(ServiceConstant.LIMIT).get(0)));
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
		if (!validateUsername(userDto.getUsername())) {
			errors.put(ErrorCode.INVALID_USER_NAME,
					EntityConstant.NAME + ValidationUtil.ERROR_RESOURCE_DELIMITER + userDto.getUsername());
		}
		if (!checkPassword(userDto.getPassword())) {
			errors.put(ErrorCode.INVALID_USER_PASSWORD, "");
		}
		return errors;
	}
}
