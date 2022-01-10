package com.epam.esm.controller.converter;

import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.epam.esm.controller.view.RoleView;
import com.epam.esm.dto.RoleDto;

@Component
public class RoleViewConverter {
	private final ModelMapper modelMapper;
	private Converter<String, String> spaceRemover = new AbstractConverter<String, String>() {
		protected String convert(String source) {
			return source == null ? null : source.trim().replaceAll("\\s+", " ");
		}
	};

	public RoleViewConverter() {
		this.modelMapper = new ModelMapper();
		modelMapper.addConverter(spaceRemover);
		modelMapper.getConfiguration().setFieldMatchingEnabled(true);
	}

	public RoleDto convertToDto(RoleView roleView) {
		return modelMapper.map(roleView, RoleDto.class);
	}

	public RoleView convertToView(RoleDto roleDto) {
		return modelMapper.map(roleDto, RoleView.class);
	}
}
