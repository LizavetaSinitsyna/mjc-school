package com.epam.esm.service.impl;

import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.epam.esm.repository.model.TagModel;
import com.epam.esm.repository.query_builder.EntityConstant;
import com.epam.esm.service.CertificateService;
import com.epam.esm.service.DateTimeWrapper;
import com.epam.esm.service.converter.CertificateConverter;
import com.epam.esm.service.converter.TagConverter;
import com.epam.esm.service.validation.CertificateValidation;
import com.epam.esm.service.validation.TagValidation;
import com.epam.esm.service.validation.Util;

/**
 * 
 * Contains methods implementation for working mostly with
 * {@code CertificateDto} entity.
 *
 */
@Service
public class CertificateServiceImpl implements CertificateService {

	private CertificateRepository certificateRepository;

	private TagRepository tagRepository;

	private CertificateValidation certificateValidation;

	private TagValidation tagValidation;

	private CertificateConverter certificateConverter;

	private TagConverter tagConverter;

	private DateTimeWrapper dateTimeWrapper;

	@Autowired
	public CertificateServiceImpl(CertificateRepository certificateRepository, TagRepository tagRepository,
			CertificateValidation certificateValidation, TagValidation tagValidation,
			CertificateConverter certificateConverter, TagConverter tagConverter, DateTimeWrapper dateTimeWrapper) {
		this.certificateRepository = certificateRepository;
		this.tagRepository = tagRepository;
		this.certificateValidation = certificateValidation;
		this.tagValidation = tagValidation;
		this.certificateConverter = certificateConverter;
		this.tagConverter = tagConverter;
		this.dateTimeWrapper = dateTimeWrapper;
	}

	/**
	 * Creates and saves the passed certificate.
	 * 
	 * @param certificateDto the certificate to be saved
	 * @return saved certificate
	 * @throws ValidationException if passed certificate contains invalid fields
	 */
	@Override
	@Transactional
	public CertificateDto create(CertificateDto certificateDto) {
		Map<ErrorCode, String> errors = certificateValidation.validateAllCertificateUpdatableFields(certificateDto);
		if (certificateRepository.certificateExistsByName(Util.removeExtraSpaces(certificateDto.getName()))) {
			errors.put(ErrorCode.DUPLICATED_CERTIFICATE_NAME,
					EntityConstant.NAME + Util.DELIMITER + certificateDto.getName());
		}

		if (!errors.isEmpty()) {
			throw new ValidationException(errors, ErrorCode.INVALID_CERTIFICATE);
		}

		LocalDateTime now = dateTimeWrapper.obtainCurrentDateTime();
		certificateDto.setCreateDate(now);
		certificateDto.setLastUpdateDate(now);

		CertificateModel createdCertificateModel = certificateRepository
				.create(certificateConverter.convertToModel(certificateDto));
		CertificateDto createdCertificate = certificateConverter.convertToDto(createdCertificateModel);
		createdCertificate.setTags(saveCertificateTags(createdCertificate.getId(), certificateDto.getTags()));

		return createdCertificate;
	}

	/**
	 * Reads certificate with passed id.
	 * 
	 * @param certificateId the id of certificate to be read
	 * @return certificate with passed id
	 * @throws ValidationException if passed certificate id is invalid
	 * @throws NotFoundException   if certificate with passed id does not exist
	 */
	@Override
	public CertificateDto readById(long certificateId) {
		if (!Util.isPositive(certificateId)) {
			throw new ValidationException(EntityConstant.ID + Util.DELIMITER + certificateId,
					ErrorCode.INVALID_CERTIFICATE_ID);
		}

		Optional<CertificateModel> certificateModel = certificateRepository.readById(certificateId);
		if (certificateModel.isEmpty()) {
			throw new NotFoundException(EntityConstant.ID + Util.DELIMITER + certificateId,
					ErrorCode.NO_CERTIFICATE_FOUND);
		}

		CertificateDto certificateDto = certificateConverter.convertToDto(certificateModel.get());
		certificateDto.setTags(readByCertificateId(certificateId));
		return certificateDto;
	}

	/**
	 * Reads all certificates according to passed parameters.
	 * 
	 * @param params the parameters which define choice of certificates and their
	 *               ordering
	 * @return certificates which meet passed parameters
	 * @throws ValidationException if passed parameters are invalid
	 */
	@Override
	public List<CertificateDto> readAll(MultiValueMap<String, String> params) {
		MultiValueMap<String, String> paramsInLowerCase = Util.mapToLowerCase(params);

		Map<ErrorCode, String> errors = certificateValidation.validateReadParams(paramsInLowerCase);
		if (!errors.isEmpty()) {
			throw new ValidationException(errors, ErrorCode.INVALID_CERTIFICATE_REQUEST_PARAMS);
		}
		List<CertificateModel> certificateModels = certificateRepository.readAll(paramsInLowerCase);
		List<CertificateDto> certificateDtos = new ArrayList<>(certificateModels.size());
		for (CertificateModel certificateModel : certificateModels) {
			CertificateDto certificateDto = certificateConverter.convertToDto(certificateModel);
			certificateDto.setTags(readByCertificateId(certificateDto.getId()));
			certificateDtos.add(certificateDto);
		}
		return certificateDtos;
	}

	/**
	 * Deletes certificate with passed id.
	 * 
	 * @param certificateId the id of certificate to be deleted
	 * @return the number of deleted certificates
	 * @param params the parameters which define choice of certificates and their
	 *               ordering
	 * @return certificates which meet passed parameters
	 * @throws ValidationException if passed certificate id is invalid
	 * @throws NotFoundException   if certificate with passed id does not exist
	 */
	@Override
	public int delete(long certificateId) {
		if (!Util.isPositive(certificateId)) {
			throw new ValidationException(EntityConstant.ID + Util.DELIMITER + certificateId,
					ErrorCode.INVALID_CERTIFICATE_ID);
		}
		int deletedCertificatesAmount = certificateRepository.delete(certificateId);
		if (deletedCertificatesAmount < 1) {
			throw new NotFoundException(EntityConstant.ID + Util.DELIMITER + certificateId,
					ErrorCode.NO_CERTIFICATE_FOUND);
		}
		return deletedCertificatesAmount;
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
					EntityConstant.NAME + Util.DELIMITER + certificateDto.getName());
		}

		if (!errors.isEmpty()) {
			throw new ValidationException(errors, ErrorCode.INVALID_CERTIFICATE);
		}

		certificateDto.setId(certificateId);
		CertificateModel certificateToUpdate = certificateConverter.convertToModel(certificateDto);

		LocalDateTime now = dateTimeWrapper.obtainCurrentDateTime();
		certificateToUpdate.setLastUpdateDate(now);

		Optional<CertificateModel> certificateModel = certificateRepository
				.updateCertificateFields(certificateToUpdate);

		if (certificateModel.isEmpty()) {
			throw new NotFoundException(EntityConstant.ID + Util.DELIMITER + certificateId,
					ErrorCode.NO_CERTIFICATE_FOUND);
		}

		CertificateDto updatedCertificate = certificateConverter.convertToDto(certificateModel.get());
		updatedCertificate.setTags(saveCertificateTags(updatedCertificate.getId(), certificateDto.getTags()));

		return readById(updatedCertificate.getId());
	}

	/**
	 * Updates entire certificate with passed id using all fields of passed
	 * certificate.
	 * 
	 * @param certificateId the id of certificate to be updated
	 * @param certificate   certificate entity which contains fields with new values
	 *                      to be set
	 * @return updated certificate
	 * @throws ValidationException if passed certificate fields are invalid
	 * @throws NotFoundException   if certificate with passed id does not exist
	 */
	@Override
	@Transactional
	public CertificateDto updateEntireCertificate(long certificateId, CertificateDto certificateDto) {
		checkCertificateExistenceById(certificateId);

		Map<ErrorCode, String> errors = certificateValidation.validateAllCertificateUpdatableFields(certificateDto);

		if (!isCertificateNameUniqueForUpdate(certificateId, certificateDto)) {
			errors.put(ErrorCode.DUPLICATED_CERTIFICATE_NAME,
					EntityConstant.NAME + Util.DELIMITER + certificateDto.getName());
		}

		if (!errors.isEmpty()) {
			throw new ValidationException(errors, ErrorCode.INVALID_CERTIFICATE);
		}

		certificateDto.setId(certificateId);
		CertificateModel certificateToUpdate = certificateConverter.convertToModel(certificateDto);

		LocalDateTime now = dateTimeWrapper.obtainCurrentDateTime();
		certificateToUpdate.setLastUpdateDate(now);

		Optional<CertificateModel> updatedCertificateModel = certificateRepository
				.updateEntireCertificate(certificateToUpdate);

		if (updatedCertificateModel.isEmpty()) {
			throw new NotFoundException(EntityConstant.ID + Util.DELIMITER + certificateId,
					ErrorCode.NO_CERTIFICATE_FOUND);
		}
		CertificateDto updatedCertificateDto = certificateConverter.convertToDto(updatedCertificateModel.get());

		updatedCertificateDto.setTags(saveCertificateTags(updatedCertificateDto.getId(), certificateDto.getTags()));

		return updatedCertificateDto;
	}

	/**
	 * Checks if certificate with passed id exists.
	 * 
	 * @param certificateId the id to be checked
	 * @throws ValidationException if passed id is not valid
	 * @throws NotFoundException   if certificate with passed id does not exist
	 */
	@Override
	public void checkCertificateExistenceById(long certificateId) {
		if (!Util.isPositive(certificateId)) {
			throw new ValidationException(EntityConstant.ID + Util.DELIMITER + certificateId,
					ErrorCode.INVALID_CERTIFICATE_ID);
		}
		Optional<CertificateModel> certificateModel = certificateRepository.readById(certificateId);

		if (certificateModel.isEmpty()) {
			throw new NotFoundException(EntityConstant.ID + Util.DELIMITER + certificateId,
					ErrorCode.NO_CERTIFICATE_FOUND);
		}

	}

	private List<TagDto> saveCertificateTags(long certificateId, List<TagDto> initialTagDtos) {
		if (initialTagDtos == null) {
			return null;
		}
		Set<TagDto> tagDtos = new HashSet<>(initialTagDtos);
		List<TagModel> tagModels = new ArrayList<>(tagDtos.size());
		List<TagDto> savedCertificateTagDtos = new ArrayList<>(tagDtos.size());

		for (TagDto tagDto : tagDtos) {
			TagModel tagModelToSave = null;
			Optional<TagModel> tagModel = tagRepository.readByName(Util.removeExtraSpaces(tagDto.getName()));
			if (tagModel.isEmpty()) {
				Map<ErrorCode, String> errors = tagValidation.validateAllTagFields(tagDto);
				if (!errors.isEmpty()) {
					throw new ValidationException(errors, ErrorCode.INVALID_TAG);
				}
				tagModelToSave = tagRepository.create(tagConverter.convertToModel(tagDto));
			} else {
				tagModelToSave = tagModel.get();
				if (tagModelToSave.isDeleted()) {
					tagModelToSave = tagRepository.restore(tagModelToSave);
				}
			}
			tagModels.add(tagModelToSave);
			savedCertificateTagDtos.add(tagConverter.convertToDto(tagModelToSave));
		}

		tagRepository.deleteAllTagsForCertificate(certificateId);
		tagRepository.saveTagsForCertificate(certificateId, tagModels);

		return savedCertificateTagDtos;
	}

	private boolean isCertificateNameUniqueForUpdate(long certificateId, CertificateDto certificateDto) {
		String testedName = Util.removeExtraSpaces(certificateDto.getName());
		Optional<CertificateModel> certificateModel = certificateRepository.readByName(testedName);
		return certificateModel.isEmpty() || certificateModel.get().getId() == certificateId;

	}

	/**
	 * Reads all tags for the certificate with passed id.
	 * 
	 * @param certificateId the id of certificate for which all tags are read
	 * @return tags for the certificate with passed id
	 */

	private List<TagDto> readByCertificateId(long certificateId) {
		List<TagModel> tagModels = tagRepository.readByCertificateId(certificateId);
		List<TagDto> tagsDto = new ArrayList<>(tagModels.size());
		for (TagModel tagModel : tagModels) {
			tagsDto.add(tagConverter.convertToDto(tagModel));
		}
		return tagsDto;
	}

}
