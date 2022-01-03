package com.epam.esm.dto;

import org.springframework.hateoas.RepresentationModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserDto extends RepresentationModel<UserDto> {
	private Long id;
	private String login;
	private RoleDto role;
}
