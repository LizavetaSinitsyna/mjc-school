package com.epam.esm.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.epam.esm.dto.UserDto;
import com.epam.esm.exception.ValidationException;
import com.epam.esm.repository.UserRepository;
import com.epam.esm.repository.model.EntityConstant;
import com.epam.esm.repository.model.UserModel;
import com.epam.esm.service.UserService;
import com.epam.esm.service.converter.UserConverter;
import com.epam.esm.service.validation.UserValidation;

class UserServiceImplTest {
	private static UserValidation userValidation;
	private static UserConverter userConverter;
	private UserRepository userRepository;
	private UserModel userModel1;
	private UserDto userDto1;
	private static UserService userService;

	private static final int OFFSET = 0;
	private static final int LIMIT = 10;
	private static final long USER_ID_1 = 1L;

	@BeforeAll
	public static void init() {
		userValidation = new UserValidation();
		userConverter = new UserConverter();
	}

	@BeforeEach
	public void setUp() {
		userRepository = Mockito.mock(UserRepository.class);
		userService = new UserServiceImpl(userRepository, userConverter, userValidation);

		userModel1 = new UserModel();
		userModel1.setLogin("user1");

		userDto1 = new UserDto();
		userDto1.setLogin("user1");

	}

	@Test
	void testReadById() {
		UserDto expected = userDto1;

		Mockito.when(userRepository.findById(USER_ID_1)).thenReturn(Optional.of(userModel1));

		UserDto actual = userService.readById(USER_ID_1);
		Assertions.assertEquals(expected, actual);

		Mockito.verify(userRepository).findById(Mockito.anyLong());
	}

	@Test
	void testReadAll() {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.put(EntityConstant.OFFSET, Arrays.asList(Integer.toString(OFFSET)));
		params.put(EntityConstant.LIMIT, Arrays.asList(Integer.toString(LIMIT)));
		List<UserDto> expected = new ArrayList<>();
		expected.add(userDto1);

		List<UserModel> userModelList = new ArrayList<>();
		userModelList.add(userModel1);

		Mockito.when(userRepository.findAll(OFFSET, LIMIT)).thenReturn(userModelList);

		List<UserDto> actual = userService.readAll(params);
		Assertions.assertEquals(expected, actual);

		Mockito.verify(userRepository).findAll(OFFSET, LIMIT);
	}

	@Test
	void testReadAllWithInvalidReadParam() {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.put(EntityConstant.SEARCH, Arrays.asList("admin"));

		Assertions.assertThrows(ValidationException.class, () -> {
			userService.readAll(params);
		});
	}

	@Test
	void testReadAllWithInvalidOffsetParam() {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.put(EntityConstant.OFFSET, Arrays.asList("one"));

		Assertions.assertThrows(ValidationException.class, () -> {
			userService.readAll(params);
		});
	}

}
