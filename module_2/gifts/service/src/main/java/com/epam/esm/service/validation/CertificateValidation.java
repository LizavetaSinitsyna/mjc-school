package com.epam.esm.service.validation;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.epam.esm.dto.CertificateDto;
import com.epam.esm.exception.CustomErrorCode;
import com.epam.esm.exception.NullEntityException;
import com.epam.esm.exception.ValidationException;
import com.epam.esm.repository.CertificateRepository;
import com.epam.esm.repository.model.CertificateModel;

@Component
public class CertificateValidation {
	private static final int MIN_STRING_LENGTH = 1;
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
			throw new ValidationException("id = " + id, CustomErrorCode.INVALID_CERTIFICATE_ID);
		}
	}

	public void validateName(String name) {
		if (name == null || name.isBlank() || name.length() < MIN_STRING_LENGTH || name.length() > MAX_NAME_LENGTH) {
			throw new ValidationException("name = " + name, CustomErrorCode.INVALID_CERTIFICATE_NAME);
		}
	}

	/*-private boolean checkCertificateNameRequirements(String certificateName) {
		return certificateName != null && !certificateName.isBlank() && certificateName.length() >= MIN_STRING_LENGTH && certificateName.length() <= MAX_NAME_LENGTH;
	}*/

	public void checkDublicatedName(String certificateName) {
		validateName(certificateName);
		CertificateModel certificateModel = certificateRepository.readByCertificateName(certificateName);
		if (certificateModel != null) {
			throw new ValidationException("name = " + certificateName, CustomErrorCode.DUPLICATED_CERTIFICATE_NAME);
		}
	}

	public void validateDescription(String description) {
		if (description.isBlank() || description.length() < MIN_STRING_LENGTH
				|| description.length() > MAX_DESCRIPTION_LENGTH) {
			throw new ValidationException("description = " + description,
					CustomErrorCode.INVALID_CERTIFICATE_DESCRIPTION);
		}
	}

	public void validatePrice(BigDecimal price) {
		if (price == null || price.compareTo(BigDecimal.ZERO) < 0 || price.compareTo(MAX_PRICE) > 0) {
			throw new ValidationException("price = " + price, CustomErrorCode.INVALID_CERTIFICATE_PRICE);
		}
	}

	public void validateDuration(int duration) {
		if (duration < MIN_DURATION || duration > MAX_DURATION) {
			throw new ValidationException("duration = " + duration, CustomErrorCode.INVALID_CERTIFICATE_DURATION);
		}
	}

	public void validateCertificateUpdatableFields(CertificateDto certificateDto) {
		if (certificateDto == null) {
			throw new NullEntityException("certificateDto = " + certificateDto, CustomErrorCode.NULL_PASSED_PARAMETER);
		}
		validateName(certificateDto.getName());
		validateDescription(certificateDto.getDescription());
		validatePrice(certificateDto.getPrice());
		validateDuration(certificateDto.getDuration());
	}
}
