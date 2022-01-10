package com.epam.esm.controller.view;

import org.springframework.hateoas.RepresentationModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TagView extends RepresentationModel<TagView> {
	private Long id;
	private String name;
}
