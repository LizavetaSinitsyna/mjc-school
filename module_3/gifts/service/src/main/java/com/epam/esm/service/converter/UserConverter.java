package com.epam.esm.service.converter;

import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.epam.esm.dto.UserDto;
import com.epam.esm.repository.model.UserModel;

@Component
public class UserConverter {
	private final ModelMapper modelMapper;
	private Converter<String, String> spaceRemover = new AbstractConverter<String, String>() {
		protected String convert(String source) {
			return source == null ? null : source.trim().replaceAll("\\s+", " ");
		}
	};

	public UserConverter() {
		this.modelMapper = new ModelMapper();
		modelMapper.addConverter(spaceRemover);
		modelMapper.getConfiguration().setFieldMatchingEnabled(true);
	}

	public UserDto convertToDto(UserModel userModel) {
		return modelMapper.map(userModel, UserDto.class);
	}

	public UserModel convertToModel(UserDto userDto) {
		return modelMapper.map(userDto, UserModel.class);
	}
}
