package com.epam.esm.controller.converter;

import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.epam.esm.controller.view.TagView;
import com.epam.esm.dto.TagDto;

@Component
public class TagViewConverter {
	private final ModelMapper modelMapper;
	private Converter<String, String> spaceRemover = new AbstractConverter<String, String>() {
		protected String convert(String source) {
			return source == null ? null : source.trim().replaceAll("\\s+", " ");
		}
	};

	public TagViewConverter() {
		this.modelMapper = new ModelMapper();
		modelMapper.addConverter(spaceRemover);
		modelMapper.getConfiguration().setFieldMatchingEnabled(true);
	}

	public TagDto convertToDto(TagView tagView) {
		return modelMapper.map(tagView, TagDto.class);
	}

	public TagView convertToView(TagDto tagDto) {
		return modelMapper.map(tagDto, TagView.class);
	}
}
