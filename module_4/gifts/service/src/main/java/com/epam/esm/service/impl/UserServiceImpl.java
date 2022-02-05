package com.epam.esm.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import com.epam.esm.dto.UserDto;
import com.epam.esm.exception.ErrorCode;
import com.epam.esm.exception.IncorrectUserCredentialsException;
import com.epam.esm.exception.NotFoundException;
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
	private final PageConverter<UserDto, UserModel> pageConverter;
	private final UserValidation userValidation;
	private final PasswordEncoder passwordEncoder;

	@Autowired
	public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, UserConverter userConverter,
			UserValidation userValidation, PageConverter<UserDto, UserModel> pageConverter,
			PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.userConverter = userConverter;
		this.pageConverter = pageConverter;
		this.userValidation = userValidation;
		this.passwordEncoder = passwordEncoder;
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
	 * Reads user with passed login and password.
	 * 
	 * @param login    the username of the user to be read
	 * @param password the password of the user to be read
	 * @return user with passed login and password
	 * @throws IncorrectUserCredentialsException if user with passed login and
	 *                                           password doesn't exist
	 */
	@Override
	public UserDto readByLoginAndPassword(String login, String password) {
		Optional<UserModel> userModel = userRepository.findByLogin(login);

		if (userModel.isEmpty() || !passwordEncoder.matches(password, userModel.get().getPassword())) {
			throw new IncorrectUserCredentialsException(ErrorCode.INCORRECT_USER_CREDENTIALS);
		}

		return userConverter.convertToDto(userModel.get());
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
	public Page<UserDto> readAll(MultiValueMap<String, String> params) {
		MultiValueMap<String, String> paramsInLowerCase = ValidationUtil.mapToLowerCase(params);
		Map<ErrorCode, String> errors = userValidation.validateReadParams(paramsInLowerCase);

		if (!errors.isEmpty()) {
			throw new ValidationException(errors, ErrorCode.INVALID_USER_REQUEST_PARAMS);
		}

		int offset = ServiceConstant.DEFAULT_PAGE_NUMBER;
		int limit = ServiceConstant.DEFAULT_LIMIT;

		if (params.containsKey(ServiceConstant.OFFSET)) {
			offset = Integer.parseInt(params.get(ServiceConstant.OFFSET).get(0));
		}

		if (params.containsKey(ServiceConstant.LIMIT)) {
			limit = Integer.parseInt(params.get(ServiceConstant.LIMIT).get(0));
		}

		Page<UserModel> pageModel = userRepository.findAll(offset, limit);
		List<UserModel> userModels = pageModel.getContent();
		List<UserDto> userDtos = new ArrayList<>(limit);
		if (userModels != null) {
			userModels.forEach(userModel -> userDtos.add(userConverter.convertToDto(userModel)));
		}

		return pageConverter.convertToDto(pageModel, userDtos);
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
		Map<ErrorCode, String> errors = userValidation.validateAllUserFields(userDto);
		if (userRepository.userExistsByLogin(ValidationUtil.removeExtraSpaces(userDto.getUsername()))) {
			errors.put(ErrorCode.DUPLICATED_USER_NAME,
					EntityConstant.NAME + ValidationUtil.ERROR_RESOURCE_DELIMITER + userDto.getUsername());
		}
		if (!errors.isEmpty()) {
			throw new ValidationException(errors, ErrorCode.INVALID_USER);
		}

		RoleModel roleModel = roleRepository.findByName(ServiceConstant.DEFAULT_ROLE_NAME).get();
		userDto.setId(null);
		UserModel userModelToSave = userConverter.convertToModel(userDto);
		userModelToSave.setRole(roleModel);
		userModelToSave.setPassword(passwordEncoder.encode(userDto.getPassword()));
		return userModelToSave;
	}

	/**
	 * Locates the user based on the username.
	 * 
	 * @param username the username identifying the user whose data is required.
	 * @return a fully populated user record (never <code>null</code>)
	 * @throws UsernameNotFoundException if user with passed username does not exist
	 */
	@Override
	public UserDto loadUserByUsername(String username) {
		UserModel userModel = userRepository.findByLogin(username)
				.orElseThrow(() -> new UsernameNotFoundException(ServiceConstant.AUTH_EXCEPTION_MESSAGE));
		return userConverter.convertToDto(userModel);
	}
}
