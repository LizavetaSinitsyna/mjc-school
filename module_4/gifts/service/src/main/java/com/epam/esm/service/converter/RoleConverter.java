package com.epam.esm.service.converter;

import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.epam.esm.dto.RoleDto;
import com.epam.esm.repository.model.RoleModel;

@Component
public class RoleConverter {
	private final ModelMapper modelMapper;
	private Converter<String, String> spaceRemover = new AbstractConverter<String, String>() {
		protected String convert(String source) {
			return source == null ? null : source.trim().replaceAll("\\s+", " ");
		}
	};

	public RoleConverter() {
		this.modelMapper = new ModelMapper();
		modelMapper.addConverter(spaceRemover);
		modelMapper.getConfiguration().setFieldMatchingEnabled(true);
	}

	public RoleDto convertToDto(RoleModel roleModel) {
		return modelMapper.map(roleModel, RoleDto.class);
	}

	public RoleModel convertToModel(RoleDto roleDto) {
		return modelMapper.map(roleDto, RoleModel.class);
	}
}
