package com.epam.esm.controller.converter;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.epam.esm.controller.view.OrderView;
import com.epam.esm.dto.OrderDto;

@Component
public class OrderViewConverter {
	private final ModelMapper modelMapper;

	public OrderViewConverter() {
		this.modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setFieldMatchingEnabled(true);
	}

	public OrderDto convertToDto(OrderView orderView) {
		return modelMapper.map(orderView, OrderDto.class);
	}

	public OrderView convertToView(OrderDto orderDto) {
		return modelMapper.map(orderDto, OrderView.class);
	}
}
