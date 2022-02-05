package com.epam.esm.controller.view;

import org.springframework.hateoas.RepresentationModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class User extends RepresentationModel<User>{
	private String username;
}
