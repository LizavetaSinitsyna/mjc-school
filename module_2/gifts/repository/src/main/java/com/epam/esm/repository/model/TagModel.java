package com.epam.esm.repository.model;

import lombok.Data;

@Data
public class TagModel {
	private long id;
	private String name;
	private boolean isDeleted;
}
