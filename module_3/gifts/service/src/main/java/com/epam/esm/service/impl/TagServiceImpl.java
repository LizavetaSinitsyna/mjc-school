package com.epam.esm.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;

import com.epam.esm.dto.TagDto;
import com.epam.esm.exception.ErrorCode;
import com.epam.esm.exception.NotFoundException;
import com.epam.esm.exception.ValidationException;
import com.epam.esm.repository.CertificateRepository;
import com.epam.esm.repository.TagRepository;
import com.epam.esm.repository.model.CertificateModel;
import com.epam.esm.repository.model.EntityConstant;
import com.epam.esm.repository.model.TagModel;
import com.epam.esm.service.ServiceConstant;
import com.epam.esm.service.TagService;
import com.epam.esm.service.converter.TagConverter;
import com.epam.esm.service.validation.TagValidation;
import com.epam.esm.service.validation.ValidationUtil;

/**
 * 
 * Contains methods implementation for working mostly with tag entities.
 *
 */
@Service
public class TagServiceImpl implements TagService {
	private CertificateRepository certificateRepository;
	private TagRepository tagRepository;
	private TagConverter tagConverter;
	private TagValidation tagValidation;

	@Autowired
	public TagServiceImpl(CertificateRepository certificateRepository, TagRepository tagRepository,
			TagConverter tagConverter, TagValidation tagValidation) {
		this.certificateRepository = certificateRepository;
		this.tagRepository = tagRepository;
		this.tagConverter = tagConverter;
		this.tagValidation = tagValidation;
	}

	/**
	 * Creates and saves the passed tag.
	 * 
	 * @param tagDto the tag to be saved
	 * @return saved tag
	 * @throws ValidationException if passed tag fields are invalid
	 */
	@Override
	public TagDto create(TagDto tagDto) {
		TagModel createdTagModel = tagRepository.save(obtainTagModelToSave(tagDto));
		TagDto createdTag = tagConverter.convertToDto(createdTagModel);
		return createdTag;
	}

	/**
	 * Creates and saves the passed tags.
	 * 
	 * @param tagDtos the tags to be saved
	 * @return saved tags
	 * @throws ValidationException if any of passed tags contains invalid fields
	 */
	@Override
	@Transactional
	public List<TagDto> createTags(List<TagDto> tagDtos) {
		List<TagDto> createdTags = null;
		if (tagDtos != null) {
			createdTags = new ArrayList<>(tagDtos.size());
			List<TagModel> tagsToSave = new ArrayList<>(tagDtos.size());
			for (TagDto tagDto : tagDtos) {
				TagModel tagModel = obtainTagModelToSave(tagDto);
				tagsToSave.add(tagModel);
			}

			List<TagModel> createdTagModels = tagRepository.saveTags(tagsToSave);
			for (TagModel tagModel : createdTagModels) {
				TagDto createdTag = tagConverter.convertToDto(tagModel);
				createdTags.add(createdTag);
			}
		}
		return createdTags;
	}

	private TagModel obtainTagModelToSave(TagDto tagDto) {
		Map<ErrorCode, String> errors = tagValidation.validateAllTagFields(tagDto);
		if (tagRepository.tagExistsByName(ValidationUtil.removeExtraSpaces(tagDto.getName()))) {
			errors.put(ErrorCode.DUPLICATED_TAG_NAME,
					EntityConstant.NAME + ValidationUtil.ERROR_RESOURCE_DELIMITER + tagDto.getName());
		}
		if (!errors.isEmpty()) {
			throw new ValidationException(errors, ErrorCode.INVALID_TAG);
		}
		tagDto.setId(null);
		return tagConverter.convertToModel(tagDto);
	}

	/**
	 * Reads tag with passed id.
	 * 
	 * @param tagId id of the tag to be read
	 * @return tag with passed id
	 * @throws ValidationException if passed tag id is invalid
	 * @throws NotFoundException   if tag with passed id does not exist
	 */
	@Override
	public TagDto readById(long tagId) {
		if (!ValidationUtil.isPositive(tagId)) {
			throw new ValidationException(EntityConstant.ID + ValidationUtil.ERROR_RESOURCE_DELIMITER + tagId,
					ErrorCode.INVALID_TAG_ID);
		}

		Optional<TagModel> tagModel = tagRepository.findById(tagId);

		if (tagModel.isEmpty()) {
			throw new NotFoundException(EntityConstant.ID + ValidationUtil.ERROR_RESOURCE_DELIMITER + tagId,
					ErrorCode.NO_TAG_FOUND);
		}

		TagDto tagDto = tagConverter.convertToDto(tagModel.get());

		return tagDto;
	}

	/**
	 * Reads all tags according to the passed parameters.
	 * 
	 * @param params the parameters which define the choice of tags and their
	 *               ordering
	 * @return tags which meet passed parameters
	 * @throws ValidationException if passed parameters are invalid
	 */
	@Override
	public List<TagDto> readAll(MultiValueMap<String, String> params) {
		MultiValueMap<String, String> paramsInLowerCase = ValidationUtil.mapToLowerCase(params);
		Map<ErrorCode, String> errors = tagValidation.validateReadParams(paramsInLowerCase);

		if (!errors.isEmpty()) {
			throw new ValidationException(errors, ErrorCode.INVALID_TAG_REQUEST_PARAMS);
		}

		int offset = ServiceConstant.OFFSET;
		int limit = ServiceConstant.LIMIT;

		if (params.containsKey(EntityConstant.OFFSET)) {
			offset = Integer.parseInt(params.get(EntityConstant.OFFSET).get(0));
		}

		if (params.containsKey(EntityConstant.LIMIT)) {
			limit = Integer.parseInt(params.get(EntityConstant.LIMIT).get(0));
		}

		List<TagModel> tagModels = tagRepository.findAll(offset, limit);
		List<TagDto> tagDtos = new ArrayList<>(tagModels.size());
		for (TagModel tagModel : tagModels) {
			tagDtos.add(tagConverter.convertToDto(tagModel));
		}
		return tagDtos;
	}

	/**
	 * Deletes tag with passed id.
	 * 
	 * @param tagId the id of the tag to be deleted
	 * @return the number of deleted tags
	 * @throws ValidationException if passed tag id is invalid
	 * @throws NotFoundException   if tag with passed id does not exist
	 */
	@Override
	@Transactional
	public int delete(long tagId) {
		if (!ValidationUtil.isPositive(tagId)) {
			throw new ValidationException(EntityConstant.ID + ValidationUtil.ERROR_RESOURCE_DELIMITER + tagId,
					ErrorCode.INVALID_TAG_ID);
		}
		if (!tagRepository.tagExistsById(tagId)) {
			throw new NotFoundException(EntityConstant.ID + ValidationUtil.ERROR_RESOURCE_DELIMITER + tagId,
					ErrorCode.NO_TAG_FOUND);
		}

		List<CertificateModel> certificates = certificateRepository.readByTagId(tagId);
		int deletedTagsAmount = tagRepository.delete(tagId);
		if (certificates != null) {
			for (CertificateModel certificate : certificates) {
				certificateRepository.delete(certificate.getId());
			}
		}
		return deletedTagsAmount;
	}

	/**
	 * Finds the most widely used tag of a user with the highest cost of all orders.
	 * 
	 * @return the most widely used tag of a user with the highest cost of all
	 *         orders
	 * @throws NotFoundException if requested tag does not exist
	 */
	@Override
	public TagDto readPopularTagByMostProfitableUser() {
		Optional<TagModel> popularTag = tagRepository.findPopularTagByMostProfitableUser();
		if (popularTag.isEmpty()) {
			throw new NotFoundException(ServiceConstant.NO_POPULAR_TAG_FOUND_MESSAGE, ErrorCode.NO_TAG_FOUND);
		}
		return tagConverter.convertToDto(popularTag.get());
	}
}
