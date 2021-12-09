package com.epam.esm.service.validation;

import com.epam.esm.dto.TagDto;
import com.epam.esm.exception.ErrorCode;
import com.epam.esm.exception.NullEntityException;
import com.epam.esm.exception.ValidationException;
import com.epam.esm.repository.TagRepository;
import com.epam.esm.repository.model.TagModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TagValidation {
	private static final int MIN_NAME_LENGTH = 1;
	private static final int MAX_NAME_LENGTH = 25;

	@Autowired
	private TagRepository tagRepository;

	public TagValidation() {

	}

	public void validateId(long id) {
		if (id <= 0) {
			throw new ValidationException("id = " + id, ErrorCode.INVALID_TAG_ID);
		}
	}

	public void validateName(String name) {
		if (name == null || name.isBlank() || name.length() < MIN_NAME_LENGTH || name.length() > MAX_NAME_LENGTH) {
			throw new ValidationException("name = " + name, ErrorCode.INVALID_TAG_NAME);
		}
	}

	public void checkDublicatedName(String tagName) {
		validateName(tagName);
		TagModel tagModel = tagRepository.readByTagName(tagName);
		if (tagModel != null) {
			throw new ValidationException("name = " + tagName, ErrorCode.DUPLICATED_TAG_NAME);
		}
	}

	public void validateTagUpdatableFields(TagDto tagDto) {
		if (tagDto == null) {
			throw new NullEntityException("tagDto = " + tagDto, ErrorCode.NULL_PASSED_PARAMETER);
		}
		validateName(tagDto.getName());
	}

}
