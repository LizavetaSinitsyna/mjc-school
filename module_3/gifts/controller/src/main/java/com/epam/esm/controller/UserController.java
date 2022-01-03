package com.epam.esm.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.epam.esm.dto.UserDto;
import com.epam.esm.service.UserService;

/**
 * Controller for working with users.
 * 
 */
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

	private UserService userService;

	@Autowired
	public UserController(UserService userService) {
		this.userService = userService;
	}

	/**
	 * Reads user with passed id.
	 * 
	 * @param userId id of the user to be read
	 * @return user with passed id
	 */
	@GetMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public UserDto readById(@PathVariable long id) {
		UserDto userDto = userService.readById(id);
		HateoasUtil.addLinksToUser(userDto);
		return userDto;
	}

	/**
	 * Reads all users according to passed parameters.
	 * 
	 * @param params the parameters which define the choice of users and their
	 *               ordering
	 * @return users which meet passed parameters
	 */
	@GetMapping
	public ResponseEntity<List<UserDto>> readAll(@RequestParam MultiValueMap<String, String> params) {
		List<UserDto> users = userService.readAll(params);
		if (users == null || users.isEmpty()) {
			return new ResponseEntity<>(users, HttpStatus.NO_CONTENT);
		} else {
			for (UserDto userDto : users) {
				HateoasUtil.addLinksToUser(userDto);
			}
			return new ResponseEntity<>(users, HttpStatus.OK);
		}
	}
}
