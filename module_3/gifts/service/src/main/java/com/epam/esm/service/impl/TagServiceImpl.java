package com.epam.esm.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;

import com.epam.esm.dto.PageDto;
import com.epam.esm.dto.TagDto;
import com.epam.esm.exception.ErrorCode;
import com.epam.esm.exception.NotFoundException;
import com.epam.esm.exception.ValidationException;
import com.epam.esm.repository.CertificateRepository;
import com.epam.esm.repository.TagRepository;
import com.epam.esm.repository.model.CertificateModel;
import com.epam.esm.repository.model.EntityConstant;
import com.epam.esm.repository.model.PageModel;
import com.epam.esm.repository.model.TagModel;
import com.epam.esm.service.ServiceConstant;
import com.epam.esm.service.TagService;
import com.epam.esm.service.converter.PageConverter;
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
	private final CertificateRepository certificateRepository;
	private final TagRepository tagRepository;
	private final TagConverter tagConverter;
	private final PageConverter<TagDto, TagModel> pageConverter;
	private final TagValidation tagValidation;

	@Autowired
	public TagServiceImpl(CertificateRepository certificateRepository, TagRepository tagRepository,
			TagConverter tagConverter, TagValidation tagValidation, PageConverter<TagDto, TagModel> pageConverter) {
		this.certificateRepository = certificateRepository;
		this.tagRepository = tagRepository;
		this.tagConverter = tagConverter;
		this.pageConverter = pageConverter;
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
		List<TagDto> createdTags = new ArrayList<>();
		if (tagDtos != null) {
			List<TagModel> tagsToSave = new ArrayList<>(tagDtos.size());
			tagDtos.forEach(tagDto -> tagsToSave.add(obtainTagModelToSave(tagDto)));
			List<TagModel> createdTagModels = tagRepository.saveTags(tagsToSave);
			createdTagModels.forEach(tagModel -> createdTags.add(tagConverter.convertToDto(tagModel)));
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

		TagModel tagModel = tagRepository.findById(tagId).orElseThrow(() -> new NotFoundException(
				EntityConstant.ID + ValidationUtil.ERROR_RESOURCE_DELIMITER + tagId, ErrorCode.NO_TAG_FOUND));

		return tagConverter.convertToDto(tagModel);
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
	public PageDto<TagDto> readAll(MultiValueMap<String, String> params) {
		MultiValueMap<String, String> paramsInLowerCase = ValidationUtil.mapToLowerCase(params);
		Map<ErrorCode, String> errors = tagValidation.validateReadParams(paramsInLowerCase);

		if (!errors.isEmpty()) {
			throw new ValidationException(errors, ErrorCode.INVALID_TAG_REQUEST_PARAMS);
		}

		int offset = ServiceConstant.DEFAULT_PAGE_NUMBER;
		int limit = ServiceConstant.DEFAULT_LIMIT;

		if (params.containsKey(ServiceConstant.OFFSET)) {
			offset = Integer.parseInt(params.get(ServiceConstant.OFFSET).get(0));
		}

		if (params.containsKey(ServiceConstant.LIMIT)) {
			limit = Integer.parseInt(params.get(ServiceConstant.LIMIT).get(0));
		}

		PageModel<TagModel> pageModel = tagRepository.findAll(offset, limit);
		List<TagModel> tagModels = pageModel.getEntities();
		List<TagDto> tagDtos = new ArrayList<>(limit);
		if (tagModels != null) {
			tagModels.forEach(tagModel -> tagDtos.add(tagConverter.convertToDto(tagModel)));
		}

		return pageConverter.convertToDto(pageModel, tagDtos);
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
			certificates.forEach(certificate -> certificateRepository.delete(certificate.getId()));
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
		TagModel popularTag = tagRepository.findPopularTagByMostProfitableUser().orElseThrow(
				() -> new NotFoundException(ServiceConstant.NO_POPULAR_TAG_FOUND_MESSAGE, ErrorCode.NO_TAG_FOUND));
		return tagConverter.convertToDto(popularTag);
	}
}
