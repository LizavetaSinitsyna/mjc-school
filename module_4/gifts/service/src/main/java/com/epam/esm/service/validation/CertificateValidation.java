package com.epam.esm.service.validation;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import com.epam.esm.dto.CertificateDto;
import com.epam.esm.exception.ErrorCode;
import com.epam.esm.repository.model.EntityConstant;
import com.epam.esm.service.ServiceConstant;

/**
 * Contains methods for certificate validation.
 *
 */
@Component
public class CertificateValidation {

	public CertificateValidation() {

	}

	/**
	 * Validates passed description requirements.
	 * 
	 * @param description the description to validate
	 * @return {@code true} if the name is valid and {@code false} otherwise
	 * @see ValidationUtil#checkLength(String, int, int)
	 */
	public boolean validateDescription(String description) {
		return ValidationUtil.checkLength(description, ServiceConstant.CERTIFICATE_MIN_DESCRIPTION_LENGTH,
				ServiceConstant.CERTIFICATE_MAX_DESCRIPTION_LENGTH);
	}

	/**
	 * Validates passed price requirements.
	 * 
	 * @param price the price to validate
	 * @return {@code true} if the price is valid and {@code false} otherwise
	 */
	public boolean validatePrice(BigDecimal price) {
		if (price == null || price.compareTo(ServiceConstant.CERTIFICATE_MIN_PRICE) < 0
				|| price.compareTo(ServiceConstant.CERTIFICATE_MAX_PRICE) > 0) {
			return false;
		}
		int scale = price.scale();
		return scale == ServiceConstant.CERTIFICATE_PRICE_SCALE;
	}

	/**
	 * Validates passed duration requirements.
	 * 
	 * @param duration the duration to validate
	 * @return {@code true} if the duration is valid and {@code false} otherwise
	 */
	public boolean validateDuration(Integer duration) {
		return duration != null && (duration >= ServiceConstant.CERTIFICATE_MIN_DURATION
				&& duration <= ServiceConstant.CERTIFICATE_MAX_DURATION);
	}

	/**
	 * Validates all certificate updatable fields.
	 * 
	 * @param certificateDto the certificate to be validated
	 * @return {@code Map} of {@code ErrorCode} as key and invalid resource as a
	 *         value for invalid fields. If all fields are valid returns empty map
	 */
	public Map<ErrorCode, String> validateAllCertificateUpdatableFields(CertificateDto certificateDto) {
		ValidationUtil.checkNull(certificateDto, EntityConstant.CERTIFICATE);
		Map<ErrorCode, String> errors = new HashMap<>();
		if (!ValidationUtil.checkLength(certificateDto.getName(), ServiceConstant.CERTIFICATE_MIN_NAME_LENGTH,
				ServiceConstant.CERTIFICATE_MAX_NAME_LENGTH)) {
			errors.put(ErrorCode.INVALID_CERTIFICATE_NAME,
					EntityConstant.NAME + ValidationUtil.ERROR_RESOURCE_DELIMITER + certificateDto.getName());
		}
		if (!validateDescription(certificateDto.getDescription())) {
			errors.put(ErrorCode.INVALID_CERTIFICATE_DESCRIPTION, EntityConstant.CERTIFICATE_DESCRIPTION
					+ ValidationUtil.ERROR_RESOURCE_DELIMITER + certificateDto.getDescription());
		}
		if (!validatePrice(certificateDto.getPrice())) {
			errors.put(ErrorCode.INVALID_CERTIFICATE_PRICE, EntityConstant.CERTIFICATE_PRICE
					+ ValidationUtil.ERROR_RESOURCE_DELIMITER + certificateDto.getPrice());
		}
		if (!validateDuration(certificateDto.getDuration())) {
			errors.put(ErrorCode.INVALID_CERTIFICATE_DURATION, EntityConstant.CERTIFICATE_DURATION
					+ ValidationUtil.ERROR_RESOURCE_DELIMITER + certificateDto.getDuration());
		}
		return errors;
	}

	/**
	 * Validates all certificate updatable not {@code null} fields.
	 * 
	 * @param certificateDto the certificate to be validated
	 * @return {@code Map} of {@code ErrorCode} as key and invalid resource as a
	 *         value for invalid fields. If all fields are valid returns empty map
	 */
	public Map<ErrorCode, String> validateAllCertificateUpdatableNotNullFields(CertificateDto certificateDto) {
		ValidationUtil.checkNull(certificateDto, EntityConstant.CERTIFICATE);

		Map<ErrorCode, String> errors = new HashMap<>();

		String name = certificateDto.getName();
		if (name != null && !ValidationUtil.checkLength(name, ServiceConstant.CERTIFICATE_MIN_NAME_LENGTH,
				ServiceConstant.CERTIFICATE_MAX_NAME_LENGTH)) {
			errors.put(ErrorCode.INVALID_CERTIFICATE_NAME,
					EntityConstant.NAME + ValidationUtil.ERROR_RESOURCE_DELIMITER + name);
		}

		String description = certificateDto.getDescription();
		if (description != null && !validateDescription(description)) {
			errors.put(ErrorCode.INVALID_CERTIFICATE_DESCRIPTION,
					EntityConstant.CERTIFICATE_DESCRIPTION + ValidationUtil.ERROR_RESOURCE_DELIMITER + description);
		}

		BigDecimal price = certificateDto.getPrice();
		if (price != null && !validatePrice(price)) {
			errors.put(ErrorCode.INVALID_CERTIFICATE_PRICE,
					EntityConstant.CERTIFICATE_PRICE + ValidationUtil.ERROR_RESOURCE_DELIMITER + price);
		}

		Integer duration = certificateDto.getDuration();
		if (duration != null && !validateDuration(duration)) {
			errors.put(ErrorCode.INVALID_CERTIFICATE_DURATION,
					EntityConstant.CERTIFICATE_DURATION + ValidationUtil.ERROR_RESOURCE_DELIMITER + duration);
		}
		return errors;
	}

	/**
	 * Validates parameters for certificates reading.
	 * 
	 * @param params the parameters for certificates reading
	 * @return {@code Map} of {@code ErrorCode} as key and invalid resource as a
	 *         value for invalid parameters. If all parameters are valid returns
	 *         empty map
	 */
	public Map<ErrorCode, String> validateReadParams(MultiValueMap<String, String> params) {
		ValidationUtil.checkNull(params, ServiceConstant.PARAMS);
		MultiValueMap<String, String> paramsInLowerCase = ValidationUtil.mapToLowerCase(params);
		Map<ErrorCode, String> errors = new HashMap<>();
		if (!ServiceConstant.CERTIFICATE_POSSIBLE_READ_PARAMS.containsAll(paramsInLowerCase.keySet())) {
			errors.put(ErrorCode.INVALID_CERTIFICATE_READ_PARAM,
					ServiceConstant.PARAMS + ValidationUtil.ERROR_RESOURCE_DELIMITER + paramsInLowerCase);
		}

		if (paramsInLowerCase.containsKey(EntityConstant.ORDER_BY)) {
			if (!ServiceConstant.CERTIFICATE_POSSIBLE_SORT_FIELD
					.containsAll(paramsInLowerCase.get(EntityConstant.ORDER_BY))) {
				errors.put(ErrorCode.INVALID_CERTIFICATE_SORT_PARAM,
						ServiceConstant.PARAMS + ValidationUtil.ERROR_RESOURCE_DELIMITER + paramsInLowerCase);
			}
		}
		if (paramsInLowerCase.containsKey(ServiceConstant.OFFSET)) {
			errors.putAll(PaginationValidation.validateOffset(paramsInLowerCase.get(ServiceConstant.OFFSET).get(0)));
		}

		if (paramsInLowerCase.containsKey(ServiceConstant.LIMIT)) {
			errors.putAll(PaginationValidation.validateLimit(paramsInLowerCase.get(ServiceConstant.LIMIT).get(0)));
		}
		return errors;
	}
}
