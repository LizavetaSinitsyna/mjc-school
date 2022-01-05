package com.epam.esm.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import com.epam.esm.dto.UserDto;
import com.epam.esm.exception.ErrorCode;
import com.epam.esm.exception.NotFoundException;
import com.epam.esm.exception.ValidationException;
import com.epam.esm.repository.RoleRepository;
import com.epam.esm.repository.UserRepository;
import com.epam.esm.repository.model.EntityConstant;
import com.epam.esm.repository.model.RoleModel;
import com.epam.esm.repository.model.UserModel;
import com.epam.esm.service.ServiceConstant;
import com.epam.esm.service.UserService;
import com.epam.esm.service.converter.UserConverter;
import com.epam.esm.service.validation.UserValidation;
import com.epam.esm.service.validation.ValidationUtil;

/**
 * 
 * Contains methods implementation for working mostly with user entities.
 *
 */
@Service
public class UserServiceImpl implements UserService {
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final UserConverter userConverter;
	private final UserValidation userValidation;

	@Autowired
	public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, UserConverter userConverter,
			UserValidation userValidation) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.userConverter = userConverter;
		this.userValidation = userValidation;
	}

	/**
	 * Reads user with passed id.
	 * 
	 * @param userId id of the user to be read
	 * @return user with passed id
	 * @throws ValidationException if passed user id is invalid
	 * @throws NotFoundException   if user with passed id does not exist
	 */
	@Override
	public UserDto readById(long userId) {
		if (!ValidationUtil.isPositive(userId)) {
			throw new ValidationException(EntityConstant.ID + ValidationUtil.ERROR_RESOURCE_DELIMITER + userId,
					ErrorCode.INVALID_USER_ID);
		}

		UserModel userModel = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
				EntityConstant.ID + ValidationUtil.ERROR_RESOURCE_DELIMITER + userId, ErrorCode.NO_USER_FOUND));

		return userConverter.convertToDto(userModel);
	}

	/**
	 * Reads all users according to the passed parameters.
	 * 
	 * @param params the parameters which define the choice of users and their
	 *               ordering
	 * @return users which meet passed parameters
	 * @throws ValidationException if passed parameters are invalid
	 */
	@Override
	public List<UserDto> readAll(MultiValueMap<String, String> params) {
		MultiValueMap<String, String> paramsInLowerCase = ValidationUtil.mapToLowerCase(params);
		Map<ErrorCode, String> errors = userValidation.validateReadParams(paramsInLowerCase);

		if (!errors.isEmpty()) {
			throw new ValidationException(errors, ErrorCode.INVALID_USER_REQUEST_PARAMS);
		}

		int offset = ServiceConstant.OFFSET;
		int limit = ServiceConstant.LIMIT;

		if (params.containsKey(EntityConstant.OFFSET)) {
			offset = Integer.parseInt(params.get(EntityConstant.OFFSET).get(0));
		}

		if (params.containsKey(EntityConstant.LIMIT)) {
			limit = Integer.parseInt(params.get(EntityConstant.LIMIT).get(0));
		}

		List<UserModel> userModels = userRepository.findAll(offset, limit);
		List<UserDto> userDtos = new ArrayList<>(userModels.size());
		userModels.forEach(userModel -> userDtos.add(userConverter.convertToDto(userModel)));

		return userDtos;
	}

	/**
	 * Creates and saves the passed user.
	 * 
	 * @param userDto the user to be saved
	 * @return saved user
	 * @throws ValidationException if passed user fields are invalid
	 */
	@Override
	@Transactional
	public UserDto create(UserDto userDto) {
		UserModel userModelToSave = obtainUserModelToSave(userDto);
		UserModel createdUserModel = userRepository.save(userModelToSave);
		UserDto createdUser = userConverter.convertToDto(createdUserModel);
		return createdUser;
	}

	/**
	 * Creates and saves the passed users.
	 * 
	 * @param userDtos the users to be saved
	 * @return saved users
	 * @throws ValidationException if any of passed users contains invalid fields
	 */
	@Override
	@Transactional
	public List<UserDto> createUsers(List<UserDto> userDtos) {
		List<UserDto> createdUsers = new ArrayList<>();
		if (userDtos != null) {
			List<UserModel> usersToSave = new ArrayList<>(userDtos.size());
			userDtos.forEach(userDto -> usersToSave.add(obtainUserModelToSave(userDto)));

			List<UserModel> createdUserModels = userRepository.saveUsers(usersToSave);
			createdUserModels.forEach(userModel -> createdUsers.add(userConverter.convertToDto(userModel)));
		}
		return createdUsers;
	}

	private UserModel obtainUserModelToSave(UserDto userDto) {
		RoleModel roleModel = roleRepository.findByName(ServiceConstant.DEFAULT_ROLE_NAME).get();
		Map<ErrorCode, String> errors = userValidation.validateAllUserFields(userDto);
		if (userRepository.userExistsByLogin(ValidationUtil.removeExtraSpaces(userDto.getLogin()))) {
			errors.put(ErrorCode.DUPLICATED_USER_NAME,
					EntityConstant.NAME + ValidationUtil.ERROR_RESOURCE_DELIMITER + userDto.getLogin());
		}
		if (!errors.isEmpty()) {
			throw new ValidationException(errors, ErrorCode.INVALID_USER);
		}
		userDto.setId(null);
		UserModel userModelToSave = userConverter.convertToModel(userDto);
		userModelToSave.setRole(roleModel);
		return userModelToSave;
	}
}
