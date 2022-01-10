package com.epam.esm.dto;

import java.util.List;

import lombok.Data;

@Data
public class PageDto<T> {
	private long currentPage;
	private long totalPagesAmount;
	private List<T> entities;
}
