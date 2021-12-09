package com.epam.esm.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.epam.esm.dto.CertificateDto;
import com.epam.esm.dto.TagDto;
import com.epam.esm.exception.ErrorCode;
import com.epam.esm.exception.DeletedEntityException;
import com.epam.esm.exception.NullEntityException;
import com.epam.esm.repository.CertificateRepository;
import com.epam.esm.repository.TagRepository;
import com.epam.esm.repository.model.CertificateModel;
import com.epam.esm.repository.model.TagModel;
import com.epam.esm.service.CertificateService;
import com.epam.esm.service.converter.CertificateConverter;
import com.epam.esm.service.converter.TagConverter;
import com.epam.esm.service.validation.CertificateValidation;
import com.epam.esm.service.validation.TagValidation;

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

	@Override
	@Transactional
	public CertificateDto create(CertificateDto certificateDto) {
		certificateValidation.validateCertificateUpdatableFields(certificateDto);

		Set<TagDto> tagDtos = new HashSet<>(certificateDto.getTags());
		List<TagModel> tagModels = new ArrayList<>(tagDtos.size());
		List<TagDto> createdCertificateTagDtos = new ArrayList<>(tagDtos.size());

		for (TagDto tagDto : tagDtos) {
			TagModel tagModel = tagRepository.readByTagName(tagDto.getName());
			if (tagModel == null) {
				tagValidation.validateTagUpdatableFields(tagDto);
				tagModel = tagRepository.create(tagConverter.convertToModel(tagDto));
			} else if (tagModel.isDeleted()) {
				tagModel = tagRepository.restore(tagModel);
			}
			tagModels.add(tagModel);
			createdCertificateTagDtos.add(tagConverter.convertToDto(tagModel));
		}
		LocalDateTime now = LocalDateTime.now();
		certificateDto.setCreateDate(now);
		certificateDto.setLastUpdateDate(now);
		CertificateModel createdCertificateModel = certificateRepository
				.create(certificateConverter.convertToModel(certificateDto));
		long createdCertificateModelId = createdCertificateModel.getId();

		tagRepository.saveTagsForCertificate(createdCertificateModelId, tagModels);

		CertificateDto createdCertificate = certificateConverter.convertToDto(createdCertificateModel);
		createdCertificate.setTags(createdCertificateTagDtos);

		return createdCertificate;
	}

	@Override
	public CertificateDto read(long certificateId) {
		certificateValidation.validateId(certificateId);
		CertificateModel certificateModel = certificateRepository.readById(certificateId);
		if (certificateModel == null) {
			throw new NullEntityException("id = " + certificateId, ErrorCode.NO_CERTIFICATE_EXISTS_WITH_REQUIRED_PARAM);
		}
		if (certificateModel.isDeleted()) {
			throw new DeletedEntityException("id = " + certificateId, ErrorCode.DELETED_CERTIFICATE);
		}
		CertificateDto certificateDto = certificateConverter.convertToDto(certificateModel);

		List<TagModel> tagModels = tagRepository.readByCertificateId(certificateId);
		List<TagDto> tagsDto = new ArrayList<>(tagModels.size());
		for (TagModel tagModel : tagModels) {
			tagsDto.add(tagConverter.convertToDto(tagModel));
		}
		certificateDto.setTags(tagsDto);

		return certificateDto;
	}

	@Override
	public List<CertificateDto> readAll(Map<String, String> filterParams) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int delete(long certificateId) {
		certificateValidation.validateId(certificateId);
		return certificateRepository.delete(certificateId);
	}

	@Override
	public CertificateDto updateCertificateFields(CertificateDto certificate) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CertificateDto updateEntireCertificate(long certificateId, CertificateDto certificateDto) {
		certificateValidation.validateId(certificateId);
		certificateValidation.validateCertificateUpdatableFields(certificateDto);
		CertificateModel certificateToUpdate = certificateConverter.convertToModel(certificateDto);
		certificateToUpdate.setId(certificateId);

		LocalDateTime now = LocalDateTime.now();
		certificateToUpdate.setLastUpdateDate(now);

		CertificateDto updatedCertificate = certificateConverter
				.convertToDto(certificateRepository.update(certificateToUpdate));
		if (updatedCertificate == null) {
			throw new NullEntityException("id = " + certificateId, ErrorCode.NO_CERTIFICATE_EXISTS_WITH_REQUIRED_PARAM);
		}

		Set<TagDto> tagDtos = new HashSet<>(certificateDto.getTags());
		List<TagModel> tagModels = new ArrayList<>(tagDtos.size());
		List<TagDto> updatedCertificateTagDtos = new ArrayList<>(tagDtos.size());

		for (TagDto tagDto : tagDtos) {
			TagModel tagModel = tagRepository.readByTagName(tagDto.getName());
			if (tagModel == null) {
				tagValidation.validateTagUpdatableFields(tagDto);
				tagModel = tagRepository.create(tagConverter.convertToModel(tagDto));
			} else if (tagModel.isDeleted()) {
				tagModel = tagRepository.restore(tagModel);
			}
			tagModels.add(tagModel);
			updatedCertificateTagDtos.add(tagConverter.convertToDto(tagModel));
		}

		tagRepository.deleteAllTagsForCertificate(certificateId);
		tagRepository.saveTagsForCertificate(certificateId, tagModels);

		updatedCertificate.setTags(updatedCertificateTagDtos);
		return updatedCertificate;
	}

}
