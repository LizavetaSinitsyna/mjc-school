package com.epam.esm.service.validation;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.epam.esm.dto.CertificateDto;
import com.epam.esm.exception.ErrorCode;
import com.epam.esm.exception.NullEntityException;
import com.epam.esm.exception.ValidationException;
import com.epam.esm.repository.CertificateRepository;
import com.epam.esm.repository.model.CertificateModel;

@Component
public class CertificateValidation {
	private static final int MIN_NAME_LENGTH = 5;
	private static final int MIN_DESCRIPTION_LENGTH = 5;
	private static final int MIN_DURATION = 1;
	private static final int MAX_DURATION = 366;
	private static final int MAX_NAME_LENGTH = 50;
	private static final int MAX_DESCRIPTION_LENGTH = 1000;
	private static final BigDecimal MAX_PRICE = new BigDecimal("99999.99");

	@Autowired
	private CertificateRepository certificateRepository;

	public CertificateValidation() {

	}

	public void validateId(long id) {
		if (id <= 0) {
			throw new ValidationException("id = " + id, ErrorCode.INVALID_CERTIFICATE_ID);
		}
	}

	public boolean validateName(String name) {
		return Util.checkLength(name, MIN_NAME_LENGTH, MAX_NAME_LENGTH);
	}

	public boolean checkDublicatedName(String certificateName) {
		if (!validateName(certificateName)) {
			return false;
		}
		CertificateModel certificateModel = certificateRepository.readByCertificateName(Util.removeExtraSpaces(certificateName));
		return certificateModel == null;
	}

	public boolean validateDescription(String description) {
		return Util.checkLength(description, MIN_DESCRIPTION_LENGTH, MAX_DESCRIPTION_LENGTH);
	}

	public boolean validatePrice(BigDecimal price) {
		return price != null && price.compareTo(BigDecimal.ZERO) > 0 && price.compareTo(MAX_PRICE) < 0;
	}

	public boolean validateDuration(int duration) {
		return duration >= MIN_DURATION && duration <= MAX_DURATION;
	}

	public void validateCertificateUpdatableFields(CertificateDto certificateDto) {
		if (certificateDto == null) {
			throw new NullEntityException("certificateDto = " + certificateDto, ErrorCode.NULL_PASSED_PARAMETER);
		}
		Map<ErrorCode, String> errors = new HashMap<>();
		if (!validateName(certificateDto.getName())) {
			errors.put(ErrorCode.INVALID_CERTIFICATE_NAME, "name = " + certificateDto.getName());
		} else if (!checkDublicatedName(certificateDto.getName())) {
			errors.put(ErrorCode.DUPLICATED_CERTIFICATE_NAME, "name = " + certificateDto.getName());
		}
		if (!validateDescription(certificateDto.getDescription())) {
			errors.put(ErrorCode.INVALID_CERTIFICATE_DESCRIPTION, "description = " + certificateDto.getDescription());
		}
		if (!validatePrice(certificateDto.getPrice())) {
			errors.put(ErrorCode.INVALID_CERTIFICATE_PRICE, "price = " + certificateDto.getPrice());
		}
		if (!validateDuration(certificateDto.getDuration())) {
			errors.put(ErrorCode.INVALID_CERTIFICATE_DURATION, "duration = " + certificateDto.getDuration());
		}
		if (!errors.isEmpty()) {
			throw new ValidationException(errors, ErrorCode.INVALID_CERTIFICATE);
		}
	}
}
