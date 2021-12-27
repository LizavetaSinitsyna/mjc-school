package com.epam.esm.dto;

import lombok.Data;

@Data
public class UserDto {
	private Long id;
	private String login;
	private Role role;
}
