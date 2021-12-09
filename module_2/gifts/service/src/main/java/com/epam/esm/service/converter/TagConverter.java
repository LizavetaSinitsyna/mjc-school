package com.epam.esm.service.converter;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.epam.esm.dto.TagDto;
import com.epam.esm.repository.model.TagModel;

@Component
public class TagConverter {
	private final ModelMapper modelMapper;
	private Converter<String, String> spaceRemover = (src) -> src.getSource().replaceAll("\\s+", " ").trim();

	public TagConverter() {
		this.modelMapper = new ModelMapper();
		modelMapper.addConverter(spaceRemover);
	}

	public TagDto convertToDto(TagModel tagModel) {
		return modelMapper.map(tagModel, TagDto.class);
	}

	public TagModel convertToModel(TagDto tagDto) {
		return modelMapper.map(tagDto, TagModel.class);
	}
}
