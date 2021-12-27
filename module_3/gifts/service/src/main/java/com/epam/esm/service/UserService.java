package com.epam.esm.service;

import java.util.List;

import org.springframework.util.MultiValueMap;

import com.epam.esm.dto.UserDto;

/**
 * 
 * Contains methods for working mostly with {@code UserDto} entity.
 *
 */
public interface UserService {
	/**
	 * Reads user with passed id.
	 * 
	 * @param userId id of user to be read
	 * @return user with passed id
	 */
	UserDto readById(long userId);

	/**
	 * Reads all users according to passed parameters.
	 * 
	 * @param params the parameters which define choice of users and their ordering
	 * @return tags which meet passed parameters
	 */
	List<UserDto> readAll(MultiValueMap<String, String> params);
}
