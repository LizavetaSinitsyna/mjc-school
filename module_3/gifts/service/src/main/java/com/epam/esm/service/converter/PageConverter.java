package com.epam.esm.service.converter;

import java.util.List;

import org.springframework.stereotype.Component;

import com.epam.esm.dto.PageDto;
import com.epam.esm.repository.model.PageModel;

@Component
public class PageConverter<T, Y> {
	public PageConverter() {
		
	}

	public PageDto<T> convertToDto(PageModel<Y> pageModel, List<T> entities) {
		PageDto<T> pageDto = new PageDto<>();
		pageDto.setCurrentPage(pageModel.getCurrentPage());
		pageDto.setTotalPagesAmount(pageModel.getTotalPagesAmount());
		pageDto.setEntities(entities);
		return pageDto;
	}

	public PageModel<T> convertToModel(PageDto<Y> pageDto, List<T> entities) {
		PageModel<T> pageModel = new PageModel<>();
		pageModel.setCurrentPage(pageDto.getCurrentPage());
		pageModel.setTotalPagesAmount(pageDto.getTotalPagesAmount());
		pageModel.setEntities(entities);
		return pageModel;
	}
}
