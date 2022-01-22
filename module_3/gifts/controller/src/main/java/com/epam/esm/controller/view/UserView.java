package com.epam.esm.controller.view;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Relation(collectionRelation = "users", itemRelation = "user")
@EqualsAndHashCode(callSuper = true)
public class UserView extends RepresentationModel<UserView> {
	private Long id;
	private String login;
	private RoleView role;
}
