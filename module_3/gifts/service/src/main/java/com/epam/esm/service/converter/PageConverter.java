package com.epam.esm.service.converter;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;

@Component
public class PageConverter<T, Y> {
	public PageConverter() {

	}

	public Page<T> convertToDto(Page<Y> pageModel, List<T> entities) {
		return new PageImpl<>(entities, pageModel.getPageable(), pageModel.getTotalElements());
	}
}
