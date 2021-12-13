package com.epam.esm.service.impl;

import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;

import com.epam.esm.dto.CertificateDto;
import com.epam.esm.dto.TagDto;
import com.epam.esm.exception.ErrorCode;
import com.epam.esm.exception.NotFoundException;
import com.epam.esm.repository.CertificateRepository;
import com.epam.esm.repository.TagRepository;
import com.epam.esm.repository.model.CertificateModel;
import com.epam.esm.repository.model.TagModel;
import com.epam.esm.repository.query_builder.EntityConstant;
import com.epam.esm.service.CertificateService;
import com.epam.esm.service.TagService;
import com.epam.esm.service.converter.CertificateConverter;
import com.epam.esm.service.converter.TagConverter;
import com.epam.esm.service.validation.CertificateValidation;
import com.epam.esm.service.validation.TagValidation;
import com.epam.esm.service.validation.Util;

/**
 * 
 * Contains methods implementation for working mostly with {@code Certificate}
 * entity.
 *
 */
@Service
public class CertificateServiceImpl implements CertificateService {

	@Autowired
	private CertificateRepository certificateRepository;

	@Autowired
	private TagRepository tagRepository;

	@Autowired
	private CertificateValidation certificateValidation;

	@Autowired
	private TagValidation tagValidation;

	@Autowired
	private CertificateConverter certificateConverter;

	@Autowired
	private TagConverter tagConverter;

	@Autowired
	private TagService tagService;

	/**
	 * Creates and saves the passed certificate.
	 * 
	 * @param certificateDto the certificate to be saved
	 * @return saved certificate
	 */
	@Override
	@Transactional
	public CertificateDto create(CertificateDto certificateDto) {
		certificateValidation.validateCertificateAllFieldsRequirementsForCreate(certificateDto);

		LocalDateTime now = LocalDateTime.now();
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
	 */
	@Override
	public CertificateDto readById(long certificateId) {
		certificateValidation.checkCertificateExistenceById(certificateId);

		CertificateDto certificateDto = certificateConverter
				.convertToDto(certificateRepository.readById(certificateId));

		certificateDto.setTags(tagService.readByCertificateId(certificateId));

		return certificateDto;
	}

	/**
	 * Reads all certificates according to passed parameters.
	 * 
	 * @param params the parameters which define choice of certificates and their
	 *               ordering
	 * @return certificates which meet passed parameters
	 */
	@Override
	public List<CertificateDto> readAll(MultiValueMap<String, String> params) {
		MultiValueMap<String, String> paramsInLowerCase = Util.mapToLowerCase(params);
		certificateValidation.validateReadParams(paramsInLowerCase);
		List<CertificateModel> certificateModels = certificateRepository.readAll(paramsInLowerCase);
		List<CertificateDto> certificateDtos = new ArrayList<>(certificateModels.size());
		for (CertificateModel certificateModel : certificateModels) {
			CertificateDto certificateDto = certificateConverter.convertToDto(certificateModel);
			certificateDto.setTags(tagService.readByCertificateId(certificateDto.getId()));
			certificateDtos.add(certificateDto);
		}
		return certificateDtos;
	}

	/**
	 * Deletes certificate with passed id.
	 * 
	 * @param certificateId the id of certificate to be deleted
	 * @return the number of deleted certificates
	 */
	@Override
	public int delete(long certificateId) {
		certificateValidation.checkCertificateExistenceById(certificateId);
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
	 */
	@Override
	@Transactional
	public CertificateDto updateCertificateFields(long certificateId, CertificateDto certificateDto) {
		certificateValidation.validateCertificateAllFieldsRequirementsForPatchUpdate(certificateId, certificateDto);

		certificateDto.setId(certificateId);
		CertificateModel certificateToUpdate = certificateConverter.convertToModel(certificateDto);

		LocalDateTime now = LocalDateTime.now();
		certificateToUpdate.setLastUpdateDate(now);

		CertificateDto updatedCertificate = certificateConverter
				.convertToDto(certificateRepository.updateCertificateFields(certificateToUpdate));
		if (updatedCertificate == null) {
			throw new NotFoundException("id = " + certificateId, ErrorCode.NO_CERTIFICATE_FOUND);
		}

		updatedCertificate.setTags(saveCertificateTags(updatedCertificate.getId(), certificateDto.getTags()));
		return updatedCertificate;
	}

	/**
	 * Updates entire certificate with passed id using all fields of passed
	 * certificate.
	 * 
	 * @param certificateId the id of certificate to be updated
	 * @param certificate   certificate entity which contains fields with new values
	 *                      to be set
	 * @return updated certificate
	 */
	@Override
	@Transactional
	public CertificateDto updateEntireCertificate(long certificateId, CertificateDto certificateDto) {
		certificateValidation.validateCertificateAllFieldsRequirementsForEntireUpdate(certificateId, certificateDto);

		certificateDto.setId(certificateId);
		CertificateModel certificateToUpdate = certificateConverter.convertToModel(certificateDto);

		LocalDateTime now = LocalDateTime.now();
		certificateToUpdate.setLastUpdateDate(now);

		CertificateDto updatedCertificate = certificateConverter
				.convertToDto(certificateRepository.updateEntireCertificate(certificateToUpdate));
		if (updatedCertificate == null) {
			throw new NotFoundException(EntityConstant.ID + Util.DELIMITER + certificateId,
					ErrorCode.NO_CERTIFICATE_FOUND);
		}

		updatedCertificate.setTags(saveCertificateTags(updatedCertificate.getId(), certificateDto.getTags()));

		return updatedCertificate;
	}

	private List<TagDto> saveCertificateTags(long certificateId, List<TagDto> initialTagDtos) {
		if (initialTagDtos == null) {
			return null;
		}
		Set<TagDto> tagDtos = new HashSet<>(initialTagDtos);
		List<TagModel> tagModels = new ArrayList<>(tagDtos.size());
		List<TagDto> savedCertificateTagDtos = new ArrayList<>(tagDtos.size());

		for (TagDto tagDto : tagDtos) {
			TagModel tagModel = tagRepository.readByName(Util.removeExtraSpaces(tagDto.getName()));
			if (tagModel == null) {
				tagValidation.validateAllTagFields(tagDto);
				tagModel = tagRepository.create(tagConverter.convertToModel(tagDto));
			} else if (tagModel.isDeleted()) {
				tagModel = tagRepository.restore(tagModel);
			}
			tagModels.add(tagModel);
			savedCertificateTagDtos.add(tagConverter.convertToDto(tagModel));
		}

		tagRepository.deleteAllTagsForCertificate(certificateId);
		tagRepository.saveTagsForCertificate(certificateId, tagModels);

		return savedCertificateTagDtos;
	}

}
