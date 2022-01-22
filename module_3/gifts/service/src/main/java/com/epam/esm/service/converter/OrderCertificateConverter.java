package com.epam.esm.service.converter;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.epam.esm.dto.OrderCertificateDto;
import com.epam.esm.repository.model.OrderCertificateModel;

@Component
public class OrderCertificateConverter {
	private ModelMapper modelMapper;

	public OrderCertificateConverter() {
		this.modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setFieldMatchingEnabled(true);
	}

	public OrderCertificateDto convertToDto(OrderCertificateModel orderCertificateModel) {
		return modelMapper.map(orderCertificateModel, OrderCertificateDto.class);
	}

	public OrderCertificateModel convertToModel(OrderCertificateDto orderCertificateDto) {
		return modelMapper.map(orderCertificateDto, OrderCertificateModel.class);
	}
}
