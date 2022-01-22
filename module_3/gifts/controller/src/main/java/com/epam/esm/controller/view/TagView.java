package com.epam.esm.controller.view;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Relation(collectionRelation = "tags", itemRelation = "tag")
@EqualsAndHashCode(callSuper = true)
public class TagView extends RepresentationModel<TagView> {
	private Long id;
	private String name;
}
