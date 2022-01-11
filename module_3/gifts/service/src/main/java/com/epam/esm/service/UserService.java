package com.epam.esm.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.util.MultiValueMap;

import com.epam.esm.dto.UserDto;

/**
 * 
 * Contains methods for working mostly with user entities.
 *
 */
public interface UserService {
	/**
	 * Reads user with passed id.
	 * 
	 * @param userId id of the user to be read
	 * @return user with passed id
	 */
	UserDto readById(long userId);

	/**
	 * Reads all users according to the passed parameters.
	 * 
	 * @param params the parameters which define the choice of users and their
	 *               ordering
	 * @return users which meet passed parameters
	 */
	Page<UserDto> readAll(MultiValueMap<String, String> params);

	/**
	 * Creates and saves the passed user.
	 * 
	 * @param userDto the user to be saved
	 * @return saved user
	 */
	UserDto create(UserDto userDto);

	/**
	 * Creates and saves the passed users.
	 * 
	 * @param userDtos the users to be saved
	 * @return saved users
	 */
	List<UserDto> createUsers(List<UserDto> userDtos);
}
