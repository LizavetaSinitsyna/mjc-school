package com.epam.esm.dto;

import org.springframework.security.core.GrantedAuthority;

import lombok.Data;

@Data
public class RoleDto implements GrantedAuthority {
	private static final long serialVersionUID = 1L;

	private Long id;
	private String name;

	@Override
	public String getAuthority() {
		return getName();
	}
}
