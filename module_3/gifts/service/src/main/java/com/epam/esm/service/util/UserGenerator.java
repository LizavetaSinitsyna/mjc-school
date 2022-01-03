package com.epam.esm.service.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.epam.esm.dto.UserDto;
import com.epam.esm.exception.GeneratorException;
import com.epam.esm.service.UserService;

@Component
public class UserGenerator {
	private UserService userService;

	@Autowired
	public UserGenerator(UserService userService) {
		this.userService = userService;
	}

	public List<UserDto> generateUsers(int amount) {
		List<UserDto> userDtosToCreate = new ArrayList<>(amount);
		ClassPathResource res = new ClassPathResource(GeneratorConstant.USER_LOGIN_FILE_PATH);
		int lineCounter = 0;
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(res.getInputStream()))) {
			String name;
			while ((name = reader.readLine()) != null && lineCounter < amount) {
				UserDto userDto = new UserDto();
				userDto.setLogin(name);
				userDtosToCreate.add(userDto);
				++lineCounter;
			}
			if (lineCounter != amount) {
				throw new GeneratorException(GeneratorConstant.NOT_ENOUGH_DATA_EXCEPTION);
			}
		} catch (IOException e) {
			throw new GeneratorException(e.getMessage());
		}
		return userService.createUsers(userDtosToCreate);
	}
}
