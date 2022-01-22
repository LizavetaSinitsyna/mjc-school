package com.epam.esm.service.converter;

import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.epam.esm.dto.TagDto;
import com.epam.esm.repository.model.TagModel;

@Component
public class TagConverter {
	private final ModelMapper modelMapper;
	private Converter<String, String> spaceRemover = new AbstractConverter<String, String>() {
		protected String convert(String source) {
			return source == null ? null : source.trim().replaceAll("\\s+", " ");
		}
	};

	public TagConverter() {
		this.modelMapper = new ModelMapper();
		modelMapper.addConverter(spaceRemover);
		modelMapper.getConfiguration().setFieldMatchingEnabled(true);
	}

	public TagDto convertToDto(TagModel tagModel) {
		return modelMapper.map(tagModel, TagDto.class);
	}

	public TagModel convertToModel(TagDto tagDto) {
		return modelMapper.map(tagDto, TagModel.class);
	}
}
