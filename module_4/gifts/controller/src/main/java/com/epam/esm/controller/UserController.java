package com.epam.esm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.epam.esm.controller.assembler.UserViewAssembler;
import com.epam.esm.controller.converter.UserRequestViewConverter;
import com.epam.esm.controller.view.AuthResponse;
import com.epam.esm.controller.view.UserRequestView;
import com.epam.esm.controller.view.UserView;
import com.epam.esm.dto.UserDto;
import com.epam.esm.security.JwtProvider;
import com.epam.esm.service.UserService;

/**
 * Controller for working with users.
 * 
 */
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

	private final UserService userService;
	private final PagedResourcesAssembler<UserDto> pagedResourcesAssembler;
	private final UserViewAssembler userViewAssembler;
	private final UserRequestViewConverter userRegistrationRequestConverter;
	private final JwtProvider jwtProvider;

	@Autowired
	public UserController(UserService userService, UserViewAssembler userViewAssembler,
			PagedResourcesAssembler<UserDto> pagedResourcesAssembler,
			UserRequestViewConverter userRegistrationRequestConverter, JwtProvider jwtProvider) {
		this.userService = userService;
		this.pagedResourcesAssembler = pagedResourcesAssembler;
		this.userViewAssembler = userViewAssembler;
		this.userRegistrationRequestConverter = userRegistrationRequestConverter;
		this.jwtProvider = jwtProvider;
	}

	/**
	 * Reads user with passed id.
	 * 
	 * @param userId id of the user to be read
	 * @return user with passed id
	 */
	@GetMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	@PreAuthorize("hasRole('ADMIN') or #id == principal.id")
	public UserView readById(@PathVariable long id) {
		UserDto userDto = userService.readById(id);
		return userViewAssembler.toModel(userDto);
	}

	/**
	 * Reads all users according to passed parameters.
	 * 
	 * @param params the parameters which define the choice of users and their
	 *               ordering
	 * @return users which meet passed parameters
	 */
	@GetMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<PagedModel<UserView>> readAll(@RequestParam MultiValueMap<String, String> params) {
		Page<UserDto> userPage = userService.readAll(params);
		PagedModel<UserView> page = pagedResourcesAssembler.toModel(userPage, userViewAssembler);
		return new ResponseEntity<>(page, HttpStatus.OK);
	}

	@PostMapping("/register")
	@ResponseStatus(HttpStatus.CREATED)
	public UserView register(@RequestBody UserRequestView userRegistrationRequest) {
		UserDto userDto = userService.create(userRegistrationRequestConverter.convertToDto(userRegistrationRequest));
		return userViewAssembler.toModel(userDto);
	}

	@PostMapping("/auth")
	@ResponseStatus(HttpStatus.OK)
	public AuthResponse authenticate(@RequestBody UserRequestView userRegistrationRequest) {
		UserDto userDto = userService.readByLoginAndPassword(userRegistrationRequest.getUsername(),
				userRegistrationRequest.getPassword());
		String token = jwtProvider.generateToken(userDto.getUsername());
		return new AuthResponse(token);
	}
}
