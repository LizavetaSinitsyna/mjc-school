package com.epam.esm.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;

import com.epam.esm.dto.TagDto;
import com.epam.esm.repository.CertificateRepository;
import com.epam.esm.repository.TagRepository;
import com.epam.esm.repository.model.CertificateModel;
import com.epam.esm.repository.model.TagModel;
import com.epam.esm.service.TagService;
import com.epam.esm.service.converter.TagConverter;
import com.epam.esm.service.validation.CertificateValidation;
import com.epam.esm.service.validation.TagValidation;
import com.epam.esm.service.validation.Util;

/**
 * 
 * Contains methods implementation for working mostly with {@code TagDto}
 * entity.
 *
 */
@Service
public class TagServiceImpl implements TagService {

	@Autowired
	private CertificateRepository certificateRepository;

	@Autowired
	private TagRepository tagRepository;

	@Autowired
	private TagConverter tagConverter;

	@Autowired
	private TagValidation tagValidation;

	@Autowired
	private CertificateValidation certificateValidation;

	/**
	 * Creates and saves the passed tag.
	 * 
	 * @param tagDto the tag to be saved
	 * @return saved tag
	 */
	@Override
	public TagDto create(TagDto tagDto) {
		tagValidation.validateAllTagFields(tagDto);
		TagModel createdTagModel = tagRepository.create(tagConverter.convertToModel(tagDto));
		TagDto createdTag = tagConverter.convertToDto(createdTagModel);
		return createdTag;
	}

	/**
	 * Reads tag with passed id.
	 * 
	 * @param tagId id of tag to be read
	 * @return tag with passed id
	 */
	@Override
	public TagDto readById(long tagId) {
		tagValidation.checkTagExistenceById(tagId);
		return tagConverter.convertToDto(tagRepository.readById(tagId));
	}

	/**
	 * Reads all tags for the certificate with passed id.
	 * 
	 * @param certificateId the id of certificate for which all tags are read
	 * @return tags for the certificate with passed id
	 */
	@Override
	public List<TagDto> readByCertificateId(long certificateId) {
		certificateValidation.checkCertificateExistenceById(certificateId);
		List<TagModel> tagModels = tagRepository.readByCertificateId(certificateId);
		List<TagDto> tagsDto = new ArrayList<>(tagModels.size());
		for (TagModel tagModel : tagModels) {
			tagsDto.add(tagConverter.convertToDto(tagModel));
		}
		return tagsDto;
	}

	/**
	 * Reads all tags according to passed parameters.
	 * 
	 * @param params the parameters which define choice of tags and their ordering
	 * @return tags which meet passed parameters
	 */
	@Override
	public List<TagDto> readAll(MultiValueMap<String, String> params) {
		MultiValueMap<String, String> paramsInLowerCase = Util.mapToLowerCase(params);
		tagValidation.validateReadParams(paramsInLowerCase);
		List<TagModel> tagModels = tagRepository.readAll(paramsInLowerCase);
		List<TagDto> tagDtos = new ArrayList<>(tagModels.size());
		for (TagModel tagModel : tagModels) {
			tagDtos.add(tagConverter.convertToDto(tagModel));
		}
		return tagDtos;
	}

	/**
	 * Deletes tag with passed id.
	 * 
	 * @param tagId the id of tag to be deleted
	 * @return the number of deleted tags
	 */
	@Override
	@Transactional
	public int delete(long tagId) {
		tagValidation.checkTagExistenceById(tagId);
		List<CertificateModel> certificates = certificateRepository.readByTagId(tagId);
		int deletedTagAmount = tagRepository.delete(tagId);
		if (certificates != null) {
			for (CertificateModel certificate : certificates) {
				certificateRepository.delete(certificate.getId());
			}
		}
		return deletedTagAmount;
	}

}
