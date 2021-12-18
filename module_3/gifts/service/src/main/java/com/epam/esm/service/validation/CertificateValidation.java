package com.epam.esm.service.validation;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import com.epam.esm.dto.CertificateDto;
import com.epam.esm.exception.ErrorCode;
import com.epam.esm.repository.query_builder.EntityConstant;

/**
 * Contains methods for certificate validation.
 *
 */
@Component
public class CertificateValidation {
	private static final int MIN_NAME_LENGTH = 5;
	private static final int MIN_DESCRIPTION_LENGTH = 5;
	private static final int MIN_DURATION = 1;
	private static final int MAX_DURATION = 366;
	private static final int MAX_NAME_LENGTH = 50;
	private static final int MAX_DESCRIPTION_LENGTH = 1000;
	private static final BigDecimal MAX_PRICE = new BigDecimal("99999.99");
	private static final Set<String> POSSIBLE_READ_PARAMS = new HashSet<String>(Arrays.asList(EntityConstant.SEARCH,
			EntityConstant.ORDER, EntityConstant.TAG, EntityConstant.PAGE, EntityConstant.LIMIT));
	private static final Set<String> POSSIBLE_SORT_FIELD = new HashSet<String>(Arrays.asList(EntityConstant.NAME,
			EntityConstant.CERTIFICATE_PRICE, EntityConstant.CERTIFICATE_CREATE_DATE,
			EntityConstant.NAME + EntityConstant.DESC_SIGN, EntityConstant.CERTIFICATE_PRICE + EntityConstant.DESC_SIGN,
			EntityConstant.CERTIFICATE_CREATE_DATE + EntityConstant.DESC_SIGN));
	private static final int DEFAULT_PAGE_NUMBER = 1;
	private static final int OFFSET = 10;

	public CertificateValidation() {

	}

	/**
	 * Validates passed description requirements.
	 * 
	 * @param description the description to validate
	 * @return {@code true} if the name is valid and {@code false} otherwise
	 * @see Util#checkLength(String, int, int)
	 */
	public boolean validateDescription(String description) {
		return Util.checkLength(description, MIN_DESCRIPTION_LENGTH, MAX_DESCRIPTION_LENGTH);
	}

	/**
	 * Validates passed price requirements.
	 * 
	 * @param price the price to validate
	 * @return {@code true} if the price is valid and {@code false} otherwise
	 */
	public boolean validatePrice(BigDecimal price) {
		return price != null && price.compareTo(BigDecimal.ZERO) > 0 && price.compareTo(MAX_PRICE) < 0;
	}

	/**
	 * Validates passed duration requirements.
	 * 
	 * @param duration the duration to validate
	 * @return {@code true} if the duration is valid and {@code false} otherwise
	 */
	public boolean validateDuration(Integer duration) {
		return duration != null && (duration >= MIN_DURATION && duration <= MAX_DURATION);
	}

	/**
	 * Validates all certificate updatable fields.
	 * 
	 * @param certificateDto the certificate to be validated
	 * @return {@code Map} of {@code ErrorCode} as key and invalid resource as a
	 *         value for invalid fields. If all fields are valid returns empty map
	 */
	public Map<ErrorCode, String> validateAllCertificateUpdatableFields(CertificateDto certificateDto) {
		Util.checkNull(certificateDto);
		Map<ErrorCode, String> errors = new HashMap<>();
		if (!Util.checkLength(certificateDto.getName(), MIN_NAME_LENGTH, MAX_NAME_LENGTH)) {
			errors.put(ErrorCode.INVALID_CERTIFICATE_NAME,
					EntityConstant.NAME + Util.DELIMITER + certificateDto.getName());
		}
		if (!validateDescription(certificateDto.getDescription())) {
			errors.put(ErrorCode.INVALID_CERTIFICATE_DESCRIPTION,
					EntityConstant.CERTIFICATE_DESCRIPTION + Util.DELIMITER + certificateDto.getDescription());
		}
		if (!validatePrice(certificateDto.getPrice())) {
			errors.put(ErrorCode.INVALID_CERTIFICATE_PRICE,
					EntityConstant.CERTIFICATE_PRICE + Util.DELIMITER + certificateDto.getPrice());
		}
		if (!validateDuration(certificateDto.getDuration())) {
			errors.put(ErrorCode.INVALID_CERTIFICATE_DURATION,
					EntityConstant.CERTIFICATE_DURATION + Util.DELIMITER + certificateDto.getDuration());
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
		Util.checkNull(certificateDto);

		Map<ErrorCode, String> errors = new HashMap<>();

		String name = certificateDto.getName();
		if (name != null && !Util.checkLength(name, MIN_NAME_LENGTH, MAX_NAME_LENGTH)) {
			errors.put(ErrorCode.INVALID_CERTIFICATE_NAME, EntityConstant.NAME + Util.DELIMITER + name);
		}

		String description = certificateDto.getDescription();
		if (description != null && !validateDescription(description)) {
			errors.put(ErrorCode.INVALID_CERTIFICATE_DESCRIPTION,
					EntityConstant.CERTIFICATE_DESCRIPTION + Util.DELIMITER + description);
		}

		BigDecimal price = certificateDto.getPrice();
		if (price != null && !validatePrice(price)) {
			errors.put(ErrorCode.INVALID_CERTIFICATE_PRICE, EntityConstant.CERTIFICATE_PRICE + Util.DELIMITER + price);
		}

		Integer duration = certificateDto.getDuration();
		if (duration != null && !validateDuration(duration)) {
			errors.put(ErrorCode.INVALID_CERTIFICATE_DURATION,
					EntityConstant.CERTIFICATE_DURATION + Util.DELIMITER + duration);
		}
		return errors;
	}

	/**
	 * Validates parameters for certificates reading.
	 * 
	 * @param paramsInLowerCase the parameters for certificates reading
	 * @return {@code Map} of {@code ErrorCode} as key and invalid resource as a
	 *         value for invalid parameters. If all parameters are valid returns
	 *         empty map
	 */
	public Map<ErrorCode, String> validateReadParams(MultiValueMap<String, String> paramsInLowerCase) {
		Util.checkNull(paramsInLowerCase);
		Map<ErrorCode, String> errors = new HashMap<>();
		if (!POSSIBLE_READ_PARAMS.containsAll(paramsInLowerCase.keySet())) {
			errors.put(ErrorCode.INVALID_CERTIFICATE_READ_PARAM,
					EntityConstant.PARAMS + Util.DELIMITER + paramsInLowerCase);
		}

		if (paramsInLowerCase.containsKey(EntityConstant.ORDER)) {
			if (!POSSIBLE_SORT_FIELD.containsAll(paramsInLowerCase.get(EntityConstant.ORDER))) {
				errors.put(ErrorCode.INVALID_CERTIFICATE_SORT_PARAM,
						EntityConstant.PARAMS + Util.DELIMITER + paramsInLowerCase);
			}
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
				limit = Integer.parseInt(paramsInLowerCase.get(EntityConstant.LIMIT).get(0));
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
