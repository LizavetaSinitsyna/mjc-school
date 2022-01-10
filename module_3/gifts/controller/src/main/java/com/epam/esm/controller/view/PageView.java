package com.epam.esm.controller.view;

import java.util.List;

import org.springframework.hateoas.RepresentationModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PageView<T> extends RepresentationModel<PageView<T>> {
	private long currentPage;
	private long totalPagesAmount;
	private List<T> entities;
}
