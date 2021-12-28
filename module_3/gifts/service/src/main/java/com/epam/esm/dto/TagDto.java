package com.epam.esm.dto;

import org.springframework.hateoas.RepresentationModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TagDto extends RepresentationModel<TagDto> {
	private Long id;
	private String name;
}
