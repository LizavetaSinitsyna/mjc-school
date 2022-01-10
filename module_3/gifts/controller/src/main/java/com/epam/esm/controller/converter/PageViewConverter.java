package com.epam.esm.controller.converter;

import java.util.List;

import org.springframework.stereotype.Component;

import com.epam.esm.controller.view.PageView;
import com.epam.esm.dto.PageDto;

@Component
public class PageViewConverter<T, Y> {
	public PageViewConverter() {

	}

	public PageDto<T> convertToDto(PageView<Y> pageView, List<T> entities) {
		PageDto<T> pageDto = new PageDto<>();
		pageDto.setCurrentPage(pageView.getCurrentPage());
		pageDto.setTotalPagesAmount(pageView.getTotalPagesAmount());
		pageDto.setEntities(entities);
		return pageDto;
	}

	public PageView<T> convertToView(PageDto<Y> pageDto, List<T> entities) {
		PageView<T> pageView = new PageView<>();
		pageView.setCurrentPage(pageDto.getCurrentPage());
		pageView.setTotalPagesAmount(pageDto.getTotalPagesAmount());
		pageView.setEntities(entities);
		return pageView;
	}
}
