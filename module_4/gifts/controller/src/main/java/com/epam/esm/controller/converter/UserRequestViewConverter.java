package com.epam.esm.controller.converter;

import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.epam.esm.controller.view.UserRequestView;
import com.epam.esm.dto.UserDto;

@Component
public class UserRequestViewConverter {
	private final ModelMapper modelMapper;
	private Converter<String, String> spaceRemover = new AbstractConverter<String, String>() {
		protected String convert(String source) {
			return source == null ? null : source.trim().replaceAll("\\s+", " ");
		}
	};

	public UserRequestViewConverter() {
		this.modelMapper = new ModelMapper();
		modelMapper.addConverter(spaceRemover);
		modelMapper.getConfiguration().setFieldMatchingEnabled(true);
	}

	public UserDto convertToDto(UserRequestView userRegistrationRequest) {
		return modelMapper.map(userRegistrationRequest, UserDto.class);
	}

	public UserRequestView convertToReqistrationRequest(UserDto userDto) {
		return modelMapper.map(userDto, UserRequestView.class);
	}
}
