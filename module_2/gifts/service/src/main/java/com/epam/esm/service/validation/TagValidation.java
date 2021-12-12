package com.epam.esm.service.validation;

import com.epam.esm.dto.TagDto;
import com.epam.esm.exception.ErrorCode;
import com.epam.esm.exception.ValidationException;
import com.epam.esm.repository.TagRepository;
import com.epam.esm.repository.query_builder.EntityConstant;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

@Component
public class TagValidation {
	private static final int MIN_NAME_LENGTH = 1;
	private static final int MAX_NAME_LENGTH = 25;
	private static final Set<String> POSSIBLE_READ_PARAMS = new HashSet<String>(Arrays.asList("page", "limit"));
	private static final int DEFAULT_PAGE_NUMBER = 1;
	private static final int OFFSET = 10;

	@Autowired
	private TagRepository tagRepository;

	public TagValidation() {

	}

	public void validateId(Long id) {
		if (id == null || id <= 0) {
			throw new ValidationException("id = " + id, ErrorCode.INVALID_TAG_ID);
		}
	}

	public void validateName(String name) {
		if (!Util.checkLength(name, MIN_NAME_LENGTH, MAX_NAME_LENGTH)) {
			throw new ValidationException("name = " + name, ErrorCode.INVALID_TAG_NAME);
		}
	}

	public void checkIsNameDublicated(String tagName) {
		String testedName = Util.removeExtraSpaces(tagName);
		validateName(testedName);
		System.out.println("\"" + testedName + "\"");
		if (tagRepository.tagExistsByName(tagName)) {
			throw new ValidationException("name = " + tagName, ErrorCode.DUPLICATED_TAG_NAME);
		}
	}

	public void validateAllTagFields(TagDto tagDto) {
		Util.checkNull(tagDto);
		validateName(tagDto.getName());
		checkIsNameDublicated(tagDto.getName());
	}

	public void validateReadParams(MultiValueMap<String, String> paramsInLowerCase) {
		Util.checkNull(paramsInLowerCase);
		if (!POSSIBLE_READ_PARAMS.containsAll(paramsInLowerCase.keySet())) {
			throw new ValidationException(EntityConstant.PARAMS + Util.DELIMITER + paramsInLowerCase,
					ErrorCode.INVALID_TAG_READ_PARAM);
		}

		if (paramsInLowerCase.containsKey(EntityConstant.PAGE)) {
			int page = DEFAULT_PAGE_NUMBER;
			String initialPage = paramsInLowerCase.get(EntityConstant.PAGE).get(0);
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
			paramsInLowerCase.put(EntityConstant.PAGE, Arrays.asList(Integer.toString(DEFAULT_PAGE_NUMBER)));
		}

		if (paramsInLowerCase.containsKey(EntityConstant.LIMIT)) {
			int limit = OFFSET;
			String initialOffset = paramsInLowerCase.get(EntityConstant.LIMIT).get(0);
			try {
				limit = Integer.parseInt(initialOffset);
			} catch (NumberFormatException e) {
				throw new ValidationException(EntityConstant.LIMIT + Util.DELIMITER + initialOffset,
						ErrorCode.INVALID_OFFSET_FORMAT);
			}
			if (limit <= 0) {
				throw new ValidationException(EntityConstant.LIMIT + Util.DELIMITER + limit, ErrorCode.NEGATIVE_OFFSET);
			}
		} else {
			paramsInLowerCase.put(EntityConstant.LIMIT, Arrays.asList(Integer.toString(OFFSET)));
		}

	}

}
