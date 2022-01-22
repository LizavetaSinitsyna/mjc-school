package com.epam.esm.controller.converter;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.epam.esm.controller.view.OrderCertificateView;
import com.epam.esm.dto.OrderCertificateDto;

@Component
public class OrderCertificateViewConverter {
	private ModelMapper modelMapper;

	public OrderCertificateViewConverter() {
		this.modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setFieldMatchingEnabled(true);
	}

	public OrderCertificateDto convertToDto(OrderCertificateView orderCertificateView) {
		return modelMapper.map(orderCertificateView, OrderCertificateDto.class);
	}

	public OrderCertificateView convertToView(OrderCertificateDto orderCertificateDto) {
		return modelMapper.map(orderCertificateDto, OrderCertificateView.class);
	}
}
