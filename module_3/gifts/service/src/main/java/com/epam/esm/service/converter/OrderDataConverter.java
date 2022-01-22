package com.epam.esm.service.converter;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.epam.esm.dto.OrderDataDto;
import com.epam.esm.repository.model.OrderModel;

@Component
public class OrderDataConverter {
	private final ModelMapper modelMapper;

	public OrderDataConverter() {
		this.modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setFieldMatchingEnabled(true);
	}

	public OrderDataDto convertToDto(OrderModel orderModel) {
		return modelMapper.map(orderModel, OrderDataDto.class);
	}

	public OrderModel convertToModel(OrderDataDto orderDataDto) {
		return modelMapper.map(orderDataDto, OrderModel.class);
	}
}
