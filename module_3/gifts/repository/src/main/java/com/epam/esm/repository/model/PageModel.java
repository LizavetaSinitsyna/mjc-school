package com.epam.esm.repository.model;

import java.util.List;

import lombok.Data;

@Data
public class PageModel<T> {
	private long currentPage;
	private long totalPagesAmount;
	private List<T> entities;
}
