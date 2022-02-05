package com.epam.esm.controller.view;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserRequestView extends User {
	private String password;
}
