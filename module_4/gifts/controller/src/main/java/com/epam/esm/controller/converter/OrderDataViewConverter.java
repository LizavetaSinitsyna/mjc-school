package com.epam.esm.controller.converter;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.epam.esm.controller.view.OrderDataView;
import com.epam.esm.dto.OrderDataDto;

@Component
public class OrderDataViewConverter {
	private final ModelMapper modelMapper;

	public OrderDataViewConverter() {
		this.modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setFieldMatchingEnabled(true);
	}

	public OrderDataDto convertToDto(OrderDataView orderView) {
		return modelMapper.map(orderView, OrderDataDto.class);
	}

	public OrderDataView convertToView(OrderDataDto orderDataDto) {
		return modelMapper.map(orderDataDto, OrderDataView.class);
	}
}
