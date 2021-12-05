package com.epam.esm.service.converter;

import com.epam.esm.dto.TagDto;
import com.epam.esm.repository.model.TagModel;

public class TagConverter {
	public static TagDto convertModelToDTO(TagModel tagModel) {
		if (tagModel == null) {
			return null;
		}

		TagDto tagDto = new TagDto();
		tagDto.setId(tagModel.getId());
		tagDto.setName(tagModel.getName());

		return tagDto;
	}

	public static TagModel convertDtoToModel(TagDto tagDto) {
		if (tagDto == null) {
			return null;
		}

		TagModel tagModel = new TagModel();
		tagModel.setId(tagDto.getId());
		tagModel.setName(tagDto.getName());

		return tagModel;
	}
}
