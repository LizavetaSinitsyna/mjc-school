package com.epam.esm.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;

import com.epam.esm.dto.CertificateDto;
import com.epam.esm.dto.TagDto;
import com.epam.esm.exception.ErrorCode;
import com.epam.esm.exception.NotFoundException;
import com.epam.esm.exception.ValidationException;
import com.epam.esm.repository.CertificateRepository;
import com.epam.esm.repository.TagRepository;
import com.epam.esm.repository.model.CertificateModel;
import com.epam.esm.repository.model.EntityConstant;
import com.epam.esm.repository.model.TagModel;
import com.epam.esm.service.CertificateService;
import com.epam.esm.service.ServiceConstant;
import com.epam.esm.service.converter.CertificateConverter;
import com.epam.esm.service.converter.PageConverter;
import com.epam.esm.service.converter.TagConverter;
import com.epam.esm.service.validation.CertificateValidation;
import com.epam.esm.service.validation.TagValidation;
import com.epam.esm.service.validation.ValidationUtil;

/**
 * 
 * Contains methods implementation for working mostly with certificate entities.
 *
 */
@Service
public class CertificateServiceImpl implements CertificateService {
	private final CertificateRepository certificateRepository;
	private final TagRepository tagRepository;
	private final CertificateValidation certificateValidation;
	private final TagValidation tagValidation;
	private final CertificateConverter certificateConverter;
	private final TagConverter tagConverter;
	private final PageConverter<CertificateDto, CertificateModel> pageConverter;

	@Autowired
	public CertificateServiceImpl(CertificateRepository certificateRepository, TagRepository tagRepository,
			CertificateValidation certificateValidation, TagValidation tagValidation,
			CertificateConverter certificateConverter, TagConverter tagConverter,
			PageConverter<CertificateDto, CertificateModel> pageConverter) {
		this.certificateRepository = certificateRepository;
		this.tagRepository = tagRepository;
		this.certificateValidation = certificateValidation;
		this.tagValidation = tagValidation;
		this.certificateConverter = certificateConverter;
		this.tagConverter = tagConverter;
		this.pageConverter = pageConverter;
	}

	/**
	 * Creates and saves the passed certificate. If tags from the passed certificate
	 * don't exist they will be created and saved as well.
	 * 
	 * @param certificateDto the certificate to be saved
	 * @return saved certificate
	 * @throws ValidationException if passed certificate contains invalid fields
	 */
	@Override
	@Transactional
	public CertificateDto create(CertificateDto certificateDto) {
		CertificateModel certificateModel = obtainCertificateModelToSave(certificateDto);
		CertificateModel createdCertificateModel = certificateRepository.save(certificateModel);
		CertificateDto createdCertificate = certificateConverter.convertToDto(createdCertificateModel);
		return createdCertificate;
	}

	/**
	 * Creates and saves the passed certificates. If tags from the passed
	 * certificates don't exist they will be created and saved as well.
	 * 
	 * @param certificateDtos the certificates to be saved
	 * @return saved certificates
	 * @throws ValidationException if any of passed certificates contains invalid
	 *                             fields
	 */
	@Override
	@Transactional
	public List<CertificateDto> createCertificates(List<CertificateDto> certificateDtos) {
		List<CertificateDto> createdCertificates = new ArrayList<>();
		if (certificateDtos != null) {
			List<CertificateModel> certificatesToSave = new ArrayList<>(certificateDtos.size());

			certificateDtos
					.forEach(certificateDto -> certificatesToSave.add(obtainCertificateModelToSave(certificateDto)));

			List<CertificateModel> createdCertificateModels = certificateRepository
					.saveCertificates(certificatesToSave);

			createdCertificateModels.forEach(
					certificateModel -> createdCertificates.add(certificateConverter.convertToDto(certificateModel)));
		}
		return createdCertificates;
	}

	private CertificateModel obtainCertificateModelToSave(CertificateDto certificateDto) {
		Map<ErrorCode, String> errors = certificateValidation.validateAllCertificateUpdatableFields(certificateDto);
		if (certificateRepository.certificateExistsByName(ValidationUtil.removeExtraSpaces(certificateDto.getName()))) {
			errors.put(ErrorCode.DUPLICATED_CERTIFICATE_NAME,
					EntityConstant.NAME + ValidationUtil.ERROR_RESOURCE_DELIMITER + certificateDto.getName());
		}

		if (!errors.isEmpty()) {
			throw new ValidationException(errors, ErrorCode.INVALID_CERTIFICATE);
		}
		certificateDto.setTags(obtainCertificateTags(certificateDto.getTags()));
		certificateDto.setId(null);
		return certificateConverter.convertToModel(certificateDto);
	}

	/**
	 * Reads certificate with passed id.
	 * 
	 * @param certificateId the id of the certificate to be read
	 * @return certificate with passed id
	 * @throws ValidationException if passed certificate id is invalid
	 * @throws NotFoundException   if certificate with passed id does not exist
	 */
	@Override
	public CertificateDto readById(long certificateId) {
		if (!ValidationUtil.isPositive(certificateId)) {
			throw new ValidationException(EntityConstant.ID + ValidationUtil.ERROR_RESOURCE_DELIMITER + certificateId,
					ErrorCode.INVALID_CERTIFICATE_ID);
		}

		CertificateModel certificateModel = certificateRepository.findById(certificateId)
				.orElseThrow(() -> new NotFoundException(
						EntityConstant.ID + ValidationUtil.ERROR_RESOURCE_DELIMITER + certificateId,
						ErrorCode.NO_CERTIFICATE_FOUND));

		return certificateConverter.convertToDto(certificateModel);
	}

	/**
	 * Reads all certificates according to the passed parameters.
	 * 
	 * @param params the parameters which define the choice of certificates and
	 *               their ordering
	 * @return certificates which meet passed parameters
	 * @throws ValidationException if passed parameters are invalid
	 */
	@Override
	public Page<CertificateDto> readAll(MultiValueMap<String, String> params) {
		MultiValueMap<String, String> paramsInLowerCase = ValidationUtil.mapToLowerCase(params);

		Map<ErrorCode, String> errors = certificateValidation.validateReadParams(paramsInLowerCase);
		if (!errors.isEmpty()) {
			throw new ValidationException(errors, ErrorCode.INVALID_CERTIFICATE_REQUEST_PARAMS);
		}

		if (paramsInLowerCase.containsKey(EntityConstant.ORDER_BY)) {
			paramsInLowerCase.put(EntityConstant.ORDER_BY,
					convertToFieldNames(paramsInLowerCase.get(EntityConstant.ORDER_BY)));
		}

		int pageNumber = ServiceConstant.DEFAULT_PAGE_NUMBER;
		int limit = ServiceConstant.DEFAULT_LIMIT;

		if (paramsInLowerCase.containsKey(ServiceConstant.OFFSET)) {
			pageNumber = Integer.parseInt(paramsInLowerCase.get(ServiceConstant.OFFSET).get(0));
		}

		if (paramsInLowerCase.containsKey(ServiceConstant.LIMIT)) {
			limit = Integer.parseInt(paramsInLowerCase.get(ServiceConstant.LIMIT).get(0));
		}

		Page<CertificateModel> pageModel = certificateRepository.findAll(paramsInLowerCase, pageNumber, limit);
		List<CertificateModel> certificateModels = pageModel.getContent();
		List<CertificateDto> certificateDtos = new ArrayList<>(limit);
		if (certificateModels != null) {
			certificateModels.forEach(
					certificateModel -> certificateDtos.add(certificateConverter.convertToDto(certificateModel)));
		}

		return pageConverter.convertToDto(pageModel, certificateDtos);
	}

	private static List<String> convertToFieldNames(List<String> sortParams) {
		List<String> sortFields = new ArrayList<String>(sortParams.size());
		for (String sortParam : sortParams) {
			StringBuilder fieldName = new StringBuilder();
			String[] words = sortParam.split("_");
			if (words.length >= 1) {
				fieldName.append(words[0].toLowerCase());
				for (int i = 1; i < words.length; i++) {
					fieldName.append(words[i].substring(0, 1).toUpperCase());
					if (words[i].length() > 1) {
						fieldName.append(words[i].substring(1).toLowerCase());
					}
				}
			}
			sortFields.add(fieldName.toString());
		}

		return sortFields;
	}

	/**
	 * Deletes certificate with passed id.
	 * 
	 * @param certificateId the id of certificate to be deleted
	 * @return the number of deleted certificates
	 * @throws ValidationException if passed certificate id is invalid
	 * @throws NotFoundException   if certificate with passed id does not exist
	 */
	@Override
	public int delete(long certificateId) {
		checkCertificateExistenceById(certificateId);
		return certificateRepository.delete(certificateId);
	}

	/**
	 * Updates certificate fields with passed id using not {@code null} fields of
	 * passed certificate entity.
	 * 
	 * @param certificateId  the id of certificate to be updated
	 * @param certificateDto certificate entity which contains fields with new
	 *                       values to be set
	 * @return updated certificate
	 * @throws ValidationException if passed certificate fields are invalid
	 * @throws NotFoundException   if certificate with passed id does not exist
	 * 
	 */
	@Override
	@Transactional
	public CertificateDto updateCertificateFields(long certificateId, CertificateDto certificateDto) {
		checkCertificateExistenceById(certificateId);

		Map<ErrorCode, String> errors = certificateValidation
				.validateAllCertificateUpdatableNotNullFields(certificateDto);

		if (!isCertificateNameUniqueForUpdate(certificateId, certificateDto)) {
			errors.put(ErrorCode.DUPLICATED_CERTIFICATE_NAME,
					EntityConstant.NAME + ValidationUtil.ERROR_RESOURCE_DELIMITER + certificateDto.getName());
		}

		if (!errors.isEmpty()) {
			throw new ValidationException(errors, ErrorCode.INVALID_CERTIFICATE);
		}

		certificateDto.setId(certificateId);
		certificateDto.setTags(obtainCertificateTags(certificateDto.getTags()));
		certificateRepository.updateCertificate(certificateConverter.convertToModel(certificateDto));

		return readById(certificateId);
	}

	/**
	 * Updates entire certificate with passed id using all fields of passed
	 * certificate.
	 * 
	 * @param certificateId the id of certificate to be updated
	 * @param certificate   certificate entity which contains fields with new values
	 *                      to be set
	 * @return updated certificate
	 * @throws ValidationException if passed certificate id or certificate fields
	 *                             are invalid
	 * @throws NotFoundException   if certificate with passed id does not exist
	 */
	@Override
	@Transactional
	public CertificateDto updateEntireCertificate(long certificateId, CertificateDto certificateDto) {
		checkCertificateExistenceById(certificateId);

		Map<ErrorCode, String> errors = certificateValidation.validateAllCertificateUpdatableFields(certificateDto);

		if (!isCertificateNameUniqueForUpdate(certificateId, certificateDto)) {
			errors.put(ErrorCode.DUPLICATED_CERTIFICATE_NAME,
					EntityConstant.NAME + ValidationUtil.ERROR_RESOURCE_DELIMITER + certificateDto.getName());
		}

		if (!errors.isEmpty()) {
			throw new ValidationException(errors, ErrorCode.INVALID_CERTIFICATE);
		}

		certificateDto.setId(certificateId);
		certificateDto.setTags(obtainCertificateTags(certificateDto.getTags()));
		certificateRepository.updateCertificate(certificateConverter.convertToModel(certificateDto));

		return readById(certificateId);
	}

	private void checkCertificateExistenceById(long certificateId) {
		if (!ValidationUtil.isPositive(certificateId)) {
			throw new ValidationException(EntityConstant.ID + ValidationUtil.ERROR_RESOURCE_DELIMITER + certificateId,
					ErrorCode.INVALID_CERTIFICATE_ID);
		}

		if (!certificateRepository.certificateExistsById(certificateId)) {
			throw new NotFoundException(EntityConstant.ID + ValidationUtil.ERROR_RESOURCE_DELIMITER + certificateId,
					ErrorCode.NO_CERTIFICATE_FOUND);
		}
	}

	private List<TagDto> obtainCertificateTags(List<TagDto> initialTagDtos) {
		if (initialTagDtos == null) {
			return new ArrayList<>();
		}
		Set<TagDto> tagDtos = new HashSet<>(initialTagDtos);
		List<TagModel> tagModels = new ArrayList<>(tagDtos.size());
		List<TagDto> obtainedCertificateTagDtos = new ArrayList<>(tagDtos.size());

		for (TagDto tagDto : tagDtos) {
			tagDto.setId(null);
			TagModel tagModelToSave = null;
			Optional<TagModel> tagModel = tagRepository.findByName(ValidationUtil.removeExtraSpaces(tagDto.getName()));
			if (tagModel.isEmpty()) {
				Map<ErrorCode, String> errors = tagValidation.validateAllTagFields(tagDto);
				if (!errors.isEmpty()) {
					throw new ValidationException(errors, ErrorCode.INVALID_TAG);
				}
				tagModelToSave = tagRepository.save(tagConverter.convertToModel(tagDto));
			} else {
				tagModelToSave = tagModel.get();
				if (tagModelToSave.isDeleted()) {
					tagRepository.restore(tagModelToSave.getId());
				}
			}
			tagModels.add(tagModelToSave);
			obtainedCertificateTagDtos.add(tagConverter.convertToDto(tagModelToSave));
		}

		return obtainedCertificateTagDtos;
	}

	private boolean isCertificateNameUniqueForUpdate(Long certificateId, CertificateDto certificateDto) {
		String name = certificateDto.getName();
		if (name == null) {
			return true;
		}
		String testedName = ValidationUtil.removeExtraSpaces(name);
		Optional<CertificateModel> certificateModel = certificateRepository.findByName(testedName);
		return certificateModel.isEmpty() || certificateModel.get().getId() == certificateId;
	}
}
