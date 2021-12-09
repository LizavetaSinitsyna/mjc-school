package com.epam.esm.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.epam.esm.dto.TagDto;
import com.epam.esm.repository.CertificateRepository;
import com.epam.esm.repository.TagRepository;
import com.epam.esm.repository.model.CertificateModel;
import com.epam.esm.repository.model.TagModel;
import com.epam.esm.service.TagService;
import com.epam.esm.service.converter.TagConverter;
import com.epam.esm.service.validation.TagValidation;

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

	@Override
	public TagDto create(TagDto tagDto) {
		tagValidation.validateTagUpdatableFields(tagDto);
		TagModel createdTagModel = tagRepository.create(tagConverter.convertToModel(tagDto));
		TagDto createdTag = tagConverter.convertToDto(createdTagModel);
		return createdTag;
	}

	@Override
	public TagDto read(long tagId) {
		tagValidation.validateId(tagId);
		TagDto tagDto = tagConverter.convertToDto(tagRepository.readById(tagId));
		return tagDto;
	}

	@Override
	public List<TagDto> readAll(Map<String, String> filterParams) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Transactional
	public int delete(long tagId) {
		tagValidation.validateId(tagId);
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
