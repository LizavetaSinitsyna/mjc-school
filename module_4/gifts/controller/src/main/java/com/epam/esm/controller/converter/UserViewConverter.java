package com.epam.esm.controller.converter;

import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.epam.esm.controller.view.User;
import com.epam.esm.dto.UserDto;

@Component
public class UserViewConverter<T extends User> {
	private final ModelMapper modelMapper;
	private Converter<String, String> spaceRemover = new AbstractConverter<String, String>() {
		protected String convert(String source) {
			return source == null ? null : source.trim().replaceAll("\\s+", " ");
		}
	};

	public UserViewConverter() {
		this.modelMapper = new ModelMapper();
		modelMapper.addConverter(spaceRemover);
		modelMapper.getConfiguration().setFieldMatchingEnabled(true);
	}

	public UserDto convertToDto(T userView) {
		return modelMapper.map(userView, UserDto.class);
	}

	public T convertToView(UserDto userDto, Class<T> destinationType) {
		return modelMapper.map(userDto, destinationType);
	}
}
