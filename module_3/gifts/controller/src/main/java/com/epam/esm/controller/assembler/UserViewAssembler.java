package com.epam.esm.controller.assembler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import com.epam.esm.controller.HateoasUtil;
import com.epam.esm.controller.UserController;
import com.epam.esm.controller.converter.UserViewConverter;
import com.epam.esm.controller.view.UserView;
import com.epam.esm.dto.UserDto;

@Component
public class UserViewAssembler extends RepresentationModelAssemblerSupport<UserDto, UserView> {

	private final UserViewConverter userConverter;

	@Autowired
	public UserViewAssembler(UserViewConverter userConverter) {
		super(UserController.class, UserView.class);
		this.userConverter = userConverter;

	}

	@Override
	public UserView toModel(UserDto entity) {
		UserView userView = userConverter.convertToView(entity);
		HateoasUtil.addLinksToUser(userView);
		return userView;
	}
}
