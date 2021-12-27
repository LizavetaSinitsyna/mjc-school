package com.epam.esm.service.converter;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.epam.esm.dto.OrderDto;
import com.epam.esm.repository.model.OrderModel;

@Component
public class OrderConverter {
	private final ModelMapper modelMapper;

	public OrderConverter() {
		this.modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setFieldMatchingEnabled(true);
	}

	public OrderDto convertToDto(OrderModel orderModel) {
		return modelMapper.map(orderModel, OrderDto.class);
	}

	public OrderModel convertToModel(OrderDto orderDto) {
		return modelMapper.map(orderDto, OrderModel.class);
	}
}
