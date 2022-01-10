package com.epam.esm.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.ArrayList;
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

import com.epam.esm.controller.converter.PageViewConverter;
import com.epam.esm.controller.converter.UserViewConverter;
import com.epam.esm.controller.view.PageView;
import com.epam.esm.controller.view.UserView;
import com.epam.esm.dto.PageDto;
import com.epam.esm.dto.UserDto;
import com.epam.esm.service.UserService;

/**
 * Controller for working with users.
 * 
 */
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

	private final UserService userService;
	private final UserViewConverter userConverter;
	private final PageViewConverter<UserView, UserDto> pageConverter;

	@Autowired
	public UserController(UserService userService, UserViewConverter userConverter,
			PageViewConverter<UserView, UserDto> pageConverter) {
		this.userService = userService;
		this.userConverter = userConverter;
		this.pageConverter = pageConverter;
	}

	/**
	 * Reads user with passed id.
	 * 
	 * @param userId id of the user to be read
	 * @return user with passed id
	 */
	@GetMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public UserView readById(@PathVariable long id) {
		UserDto userDto = userService.readById(id);
		UserView userView = userConverter.convertToView(userDto);
		HateoasUtil.addLinksToUser(userView);
		return userView;
	}

	/**
	 * Reads all users according to passed parameters.
	 * 
	 * @param params the parameters which define the choice of users and their
	 *               ordering
	 * @return users which meet passed parameters
	 */
	@GetMapping
	public ResponseEntity<PageView<UserView>> readAll(@RequestParam MultiValueMap<String, String> params) {
		PageDto<UserDto> userPage = userService.readAll(params);
		List<UserDto> users = userPage.getEntities();
		if (userPage == null || users == null || users.isEmpty()) {
			return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
		} else {
			List<UserView> usersView = new ArrayList<>(users.size());
			users.forEach(userDto -> usersView.add(userConverter.convertToView(userDto)));
			PageView<UserView> userPageView = pageConverter.convertToView(userPage, usersView);
			usersView.forEach(userView -> HateoasUtil.addLinksToUser(userView));
			HateoasUtil.addLinksToPage(userPageView, linkTo(methodOn(TagController.class).readAll(params)));
			return new ResponseEntity<>(userPageView, HttpStatus.OK);
		}
	}
}
