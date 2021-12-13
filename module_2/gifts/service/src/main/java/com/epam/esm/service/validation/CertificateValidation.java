package com.epam.esm.service.validation;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import com.epam.esm.dto.CertificateDto;
import com.epam.esm.exception.DeletedEntityException;
import com.epam.esm.exception.ErrorCode;
import com.epam.esm.exception.NotFoundException;
import com.epam.esm.exception.ValidationException;
import com.epam.esm.repository.CertificateRepository;
import com.epam.esm.repository.model.CertificateModel;
import com.epam.esm.repository.query_builder.EntityConstant;

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

	@Autowired
	private CertificateRepository certificateRepository;

	public CertificateValidation() {

	}

	public void validateId(Long id) {
		if (id == null || id <= 0) {
			throw new ValidationException(EntityConstant.ID + Util.DELIMITER + id, ErrorCode.INVALID_CERTIFICATE_ID);
		}
	}

	public boolean validateName(String name) {
		return Util.checkLength(name, MIN_NAME_LENGTH, MAX_NAME_LENGTH);
	}

	public boolean checkIsNameDublicated(String certificateName) {
		String testedName = Util.removeExtraSpaces(certificateName);

		if (!validateName(testedName)) {
			return false;
		}
		return certificateRepository.certificateExistsByName(testedName);
	}

	public void checkCertificateExistenceById(long certificateId) {
		validateId(certificateId);
		CertificateModel certificateModel = certificateRepository.readById(certificateId);

		if (certificateModel == null) {
			throw new NotFoundException(EntityConstant.ID + Util.DELIMITER + certificateId,
					ErrorCode.NO_CERTIFICATE_FOUND);
		}
		if (certificateModel.isDeleted()) {
			throw new DeletedEntityException(EntityConstant.ID + Util.DELIMITER + certificateId,
					ErrorCode.DELETED_CERTIFICATE);
		}
	}

	public boolean validateDescription(String description) {
		return Util.checkLength(description, MIN_DESCRIPTION_LENGTH, MAX_DESCRIPTION_LENGTH);
	}

	public boolean validatePrice(BigDecimal price) {
		return price != null && price.compareTo(BigDecimal.ZERO) > 0 && price.compareTo(MAX_PRICE) < 0;
	}

	public boolean validateDuration(Integer duration) {
		return duration != null && (duration >= MIN_DURATION && duration <= MAX_DURATION);
	}

	public void validateCertificateAllFieldsRequirementsForCreate(CertificateDto certificateDto) {
		Util.checkNull(certificateDto);
		Map<ErrorCode, String> errors = new HashMap<>();
		errors.putAll(validateAllCertificateUpdatableFields(certificateDto));
		errors.putAll(validateCertificateUniqueFieldsForCreate(certificateDto));
		if (!errors.isEmpty()) {
			throw new ValidationException(errors, ErrorCode.INVALID_CERTIFICATE);
		}
	}

	public void validateCertificateAllFieldsRequirementsForEntireUpdate(long certificateId,
			CertificateDto certificateDto) {
		Util.checkNull(certificateDto);
		checkCertificateExistenceById(certificateId);
		Map<ErrorCode, String> errors = new HashMap<>();
		errors.putAll(validateAllCertificateUpdatableFields(certificateDto));
		errors.putAll(validateCertificateUniqueFieldsForUpdate(certificateId, certificateDto));
		if (!errors.isEmpty()) {
			throw new ValidationException(errors, ErrorCode.INVALID_CERTIFICATE);
		}
	}

	public void validateCertificateAllFieldsRequirementsForPatchUpdate(long certificateId,
			CertificateDto certificateDto) {
		Util.checkNull(certificateDto);
		checkCertificateExistenceById(certificateId);
		Map<ErrorCode, String> errors = new HashMap<>();
		errors.putAll(validateAllCertificateUpdatableNotNullFields(certificateDto));
		errors.putAll(validateCertificateUniqueFieldsForUpdate(certificateId, certificateDto));
		if (!errors.isEmpty()) {
			throw new ValidationException(errors, ErrorCode.INVALID_CERTIFICATE);
		}
	}

	private Map<ErrorCode, String> validateCertificateUniqueFieldsForCreate(CertificateDto certificateDto) {
		Map<ErrorCode, String> errors = new HashMap<>();
		if (checkIsNameDublicated(certificateDto.getName())) {
			errors.put(ErrorCode.DUPLICATED_CERTIFICATE_NAME,
					EntityConstant.NAME + Util.DELIMITER + certificateDto.getName());
		}
		return errors;
	}

	private Map<ErrorCode, String> validateCertificateUniqueFieldsForUpdate(long certificateId,
			CertificateDto certificateDto) {
		Map<ErrorCode, String> errors = new HashMap<>();

		String testedName = Util.removeExtraSpaces(certificateDto.getName());

		CertificateModel certificateModel = certificateRepository.readByName(testedName);
		if (certificateModel != null && certificateModel.getId() != certificateDto.getId()) {
			errors.put(ErrorCode.DUPLICATED_CERTIFICATE_NAME,
					EntityConstant.NAME + Util.DELIMITER + certificateDto.getName());
		}
		return errors;

	}

	private Map<ErrorCode, String> validateAllCertificateUpdatableFields(CertificateDto certificateDto) {
		Map<ErrorCode, String> errors = new HashMap<>();
		if (!validateName(certificateDto.getName())) {
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

	private Map<ErrorCode, String> validateAllCertificateUpdatableNotNullFields(CertificateDto certificateDto) {

		Map<ErrorCode, String> errors = new HashMap<>();

		String name = certificateDto.getName();
		if (name != null && !validateName(name)) {
			errors.put(ErrorCode.INVALID_CERTIFICATE_NAME,
					EntityConstant.NAME + Util.DELIMITER + certificateDto.getName());
		}

		String description = certificateDto.getDescription();
		if (description != null && !validateDescription(description)) {
			errors.put(ErrorCode.INVALID_CERTIFICATE_DESCRIPTION,
					EntityConstant.CERTIFICATE_DESCRIPTION + Util.DELIMITER + certificateDto.getDescription());
		}

		BigDecimal price = certificateDto.getPrice();
		if (price != null && !validatePrice(price)) {
			errors.put(ErrorCode.INVALID_CERTIFICATE_PRICE,
					EntityConstant.CERTIFICATE_PRICE + Util.DELIMITER + certificateDto.getPrice());
		}

		Integer duration = certificateDto.getDuration();
		if (duration != null && !validateDuration(duration)) {
			errors.put(ErrorCode.INVALID_CERTIFICATE_DURATION,
					EntityConstant.CERTIFICATE_DURATION + Util.DELIMITER + certificateDto.getDuration());
		}
		return errors;
	}

	public void validateReadParams(MultiValueMap<String, String> params) {
		Util.checkNull(params);
		if (!POSSIBLE_READ_PARAMS.containsAll(params.keySet())) {
			throw new ValidationException(EntityConstant.PARAMS + Util.DELIMITER + params,
					ErrorCode.INVALID_CERTIFICATE_READ_PARAM);
		}

		if (params.containsKey(EntityConstant.ORDER)) {
			if (!POSSIBLE_SORT_FIELD.containsAll(params.get(EntityConstant.ORDER))) {
				throw new ValidationException(EntityConstant.PARAMS + Util.DELIMITER + params,
						ErrorCode.INVALID_CERTIFICATE_SORT_PARAM);
			}
		}
		if (params.containsKey(EntityConstant.PAGE)) {
			int page = DEFAULT_PAGE_NUMBER;
			String initialPage = params.get(EntityConstant.PAGE).get(0);
			try {
				page = Integer.parseInt(initialPage);
			} catch (NumberFormatException e) {
				throw new ValidationException(EntityConstant.PAGE + Util.DELIMITER + initialPage,
						ErrorCode.INVALID_PAGE_FORMAT);
			}
			if (page <= 0) {
				throw new ValidationException(EntityConstant.PAGE + Util.DELIMITER + page,
						ErrorCode.NEGATIVE_PAGE_NUMBER);
			}
		} else {
			params.put(EntityConstant.PAGE, Arrays.asList(Integer.toString(DEFAULT_PAGE_NUMBER)));
		}

		if (params.containsKey(EntityConstant.LIMIT)) {
			int limit = OFFSET;
			try {
				limit = Integer.parseInt(params.get(EntityConstant.LIMIT).get(0));
			} catch (NumberFormatException e) {
				throw new ValidationException(EntityConstant.LIMIT + Util.DELIMITER + limit,
						ErrorCode.INVALID_OFFSET_FORMAT);
			}
			if (limit <= 0) {
				throw new ValidationException(EntityConstant.LIMIT + Util.DELIMITER + limit, ErrorCode.NEGATIVE_OFFSET);
			}
		} else {
			params.put(EntityConstant.LIMIT, Arrays.asList(Integer.toString(OFFSET)));
		}
	}

}
