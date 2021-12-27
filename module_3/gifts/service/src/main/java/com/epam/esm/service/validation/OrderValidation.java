package com.epam.esm.service.validation;

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
public class OrderValidation {
	private static final Set<String> POSSIBLE_READ_PARAMS = new HashSet<String>(Arrays.asList("offset", "limit"));

	public OrderValidation() {

	}

	/**
	 * Validates parameters for orders reading.
	 * 
	 * @param paramsInLowerCase the parameters for orders reading
	 * @return {@code Map} of {@code ErrorCode} as key and invalid resource as a
	 *         value for invalid parameters. If all parameters are valid returns
	 *         empty map
	 */
	public Map<ErrorCode, String> validateReadParams(MultiValueMap<String, String> params) {
		Util.checkNull(params);
		MultiValueMap<String, String> paramsInLowerCase = Util.mapToLowerCase(params);
		Map<ErrorCode, String> errors = new HashMap<>();
		if (!POSSIBLE_READ_PARAMS.containsAll(paramsInLowerCase.keySet())) {
			errors.put(ErrorCode.INVALID_ORDER_READ_PARAM, EntityConstant.PARAMS + Util.ERROR_RESOURCE_DELIMITER + params);
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
