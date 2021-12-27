package com.epam.esm.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import com.epam.esm.dto.UserDto;
import com.epam.esm.exception.ErrorCode;
import com.epam.esm.exception.NotFoundException;
import com.epam.esm.exception.ValidationException;
import com.epam.esm.repository.UserRepository;
import com.epam.esm.repository.model.EntityConstant;
import com.epam.esm.repository.model.UserModel;
import com.epam.esm.service.UserService;
import com.epam.esm.service.converter.UserConverter;
import com.epam.esm.service.validation.UserValidation;
import com.epam.esm.service.validation.Util;

/**
 * 
 * Contains methods implementation for working mostly with {@code UserDto}
 * entity.
 *
 */
@Service
public class UserServiceImpl implements UserService {
	private static final int OFFSET = 0;
	private static final int LIMIT = 10;

	private UserRepository userRepository;

	private UserConverter userConverter;

	private UserValidation userValidation;

	@Autowired
	public UserServiceImpl(UserRepository userRepository, UserConverter userConverter, UserValidation userValidation) {
		this.userRepository = userRepository;
		this.userConverter = userConverter;
		this.userValidation = userValidation;
	}

	/**
	 * Reads user with passed id.
	 * 
	 * @param userId id of user to be read
	 * @return user with passed id
	 */
	@Override
	public UserDto readById(long userId) {
		if (!Util.isPositive(userId)) {
			throw new ValidationException(EntityConstant.ID + Util.ERROR_RESOURCE_DELIMITER + userId, ErrorCode.INVALID_USER_ID);
		}

		Optional<UserModel> userModel = userRepository.findById(userId);

		if (userModel.isEmpty()) {
			throw new NotFoundException(EntityConstant.ID + Util.ERROR_RESOURCE_DELIMITER + userId, ErrorCode.NO_USER_FOUND);
		}

		UserDto userDto = userConverter.convertToDto(userModel.get());

		return userDto;
	}

	/**
	 * Reads all users according to passed parameters.
	 * 
	 * @param params the parameters which define choice of users and their ordering
	 * @return users which meet passed parameters
	 */
	@Override
	public List<UserDto> readAll(MultiValueMap<String, String> params) {
		MultiValueMap<String, String> paramsInLowerCase = Util.mapToLowerCase(params);
		Map<ErrorCode, String> errors = userValidation.validateReadParams(paramsInLowerCase);

		if (!errors.isEmpty()) {
			throw new ValidationException(errors, ErrorCode.INVALID_USER_REQUEST_PARAMS);
		}

		int offset = OFFSET;
		int limit = LIMIT;

		if (params.containsKey(EntityConstant.OFFSET)) {
			offset = Integer.parseInt(params.get(EntityConstant.OFFSET).get(0));
		}

		if (params.containsKey(EntityConstant.LIMIT)) {
			limit = Integer.parseInt(params.get(EntityConstant.LIMIT).get(0));
		}

		List<UserModel> userModels = userRepository.findAll(offset, limit);
		List<UserDto> userDtos = new ArrayList<>(userModels.size());
		for (UserModel userModel : userModels) {
			userDtos.add(userConverter.convertToDto(userModel));
		}
		return userDtos;
	}

}
