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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.epam.esm.dto.RoleDto;
import com.epam.esm.dto.UserDto;
import com.epam.esm.exception.ValidationException;
import com.epam.esm.repository.RoleRepository;
import com.epam.esm.repository.UserRepository;
import com.epam.esm.repository.model.EntityConstant;
import com.epam.esm.repository.model.RoleModel;
import com.epam.esm.repository.model.UserModel;
import com.epam.esm.service.ServiceConstant;
import com.epam.esm.service.UserService;
import com.epam.esm.service.converter.PageConverter;
import com.epam.esm.service.converter.UserConverter;
import com.epam.esm.service.validation.UserValidation;

class UserServiceImplTest {
	private static final long USER_ID_1 = 1L;

	private static UserValidation userValidation;
	private static UserConverter userConverter;
	private static PageConverter<UserDto, UserModel> pageConverter;
	private UserRepository userRepository;
	private RoleRepository roleRepository;
	private UserModel userModel1;
	private UserDto userDto1;
	private RoleModel roleModel1;
	private RoleDto roleDto1;
	private static UserService userService;
	private Page<UserModel> userModelsPage;
	private Page<UserDto> userDtosPage;

	@BeforeAll
	public static void init() {
		userValidation = new UserValidation();
		userConverter = new UserConverter();
		pageConverter = new PageConverter<>();
	}

	@BeforeEach
	public void setUp() {
		userRepository = Mockito.mock(UserRepository.class);
		roleRepository = Mockito.mock(RoleRepository.class);
		userService = new UserServiceImpl(userRepository, roleRepository, userConverter, userValidation, pageConverter);

		roleModel1 = new RoleModel();
		roleModel1.setName("user");

		userModel1 = new UserModel();
		userModel1.setLogin("user1");
		userModel1.setRole(roleModel1);

		roleDto1 = new RoleDto();
		roleDto1.setName("user");

		userDto1 = new UserDto();
		userDto1.setLogin("user1");
		userDto1.setRole(roleDto1);

		List<UserDto> userDtos = new ArrayList<>();
		userDtos.add(userDto1);

		List<UserModel> userModels = new ArrayList<>();
		userModels.add(userModel1);

		Pageable pageable = PageRequest.of(ServiceConstant.DEFAULT_PAGE_NUMBER, ServiceConstant.DEFAULT_LIMIT);
		userModelsPage = new PageImpl<>(userModels, pageable, userModels.size());
		userDtosPage = new PageImpl<>(userDtos, pageable, userDtos.size());
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

		Page<UserDto> expected = userDtosPage;

		Mockito.when(userRepository.findAll(ServiceConstant.DEFAULT_PAGE_NUMBER, ServiceConstant.DEFAULT_LIMIT))
				.thenReturn(userModelsPage);

		Page<UserDto> actual = userService.readAll(params);
		Assertions.assertEquals(expected, actual);

		Mockito.verify(userRepository).findAll(ServiceConstant.DEFAULT_PAGE_NUMBER, ServiceConstant.DEFAULT_LIMIT);
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
		params.put(ServiceConstant.OFFSET, Arrays.asList("one"));

		Assertions.assertThrows(ValidationException.class, () -> {
			userService.readAll(params);
		});
	}

	@Test
	void testCreate() {
		UserDto expected = userDto1;

		Mockito.when(userRepository.save(Mockito.any())).thenReturn(userModel1);
		Mockito.when(userRepository.userExistsByLogin(Mockito.any())).thenReturn(false);
		Mockito.when(roleRepository.findByName(ServiceConstant.DEFAULT_ROLE_NAME)).thenReturn(Optional.of(roleModel1));

		UserDto actual = userService.create(expected);

		Assertions.assertEquals(expected, actual);

		Mockito.verify(userRepository).save(Mockito.any());
		Mockito.verify(userRepository).userExistsByLogin(Mockito.any());
	}

	@Test
	void testCreateUsers() {
		List<UserDto> expected = new ArrayList<>();
		expected.add(userDto1);

		Mockito.when(userRepository.saveUsers(Mockito.any())).thenReturn(Arrays.asList(userModel1));
		Mockito.when(userRepository.userExistsByLogin(Mockito.any())).thenReturn(false);
		Mockito.when(roleRepository.findByName(ServiceConstant.DEFAULT_ROLE_NAME)).thenReturn(Optional.of(roleModel1));

		List<UserDto> actual = userService.createUsers(expected);

		Assertions.assertEquals(expected, actual);

		Mockito.verify(userRepository).saveUsers(Mockito.any());
		Mockito.verify(userRepository).userExistsByLogin(Mockito.any());
	}
}
