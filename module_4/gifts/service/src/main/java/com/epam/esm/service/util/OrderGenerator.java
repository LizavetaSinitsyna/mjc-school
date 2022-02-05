package com.epam.esm.service.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.epam.esm.dto.CertificateDto;
import com.epam.esm.dto.OrderCertificateDto;
import com.epam.esm.dto.OrderDto;
import com.epam.esm.dto.UserDto;
import com.epam.esm.service.OrderService;

@Component
public class OrderGenerator {
	private final OrderService orderService;

	@Autowired
	public OrderGenerator(OrderService orderService) {
		this.orderService = orderService;
	}

	public List<OrderDto> generateOrders(int ordersAmount, List<CertificateDto> existedCertificates,
			List<UserDto> existedUsers, int orderCertificatesMinAmount, int orderCertificatesMaxAmount,
			int orderCertificatesUniqueMinAmount, int orderCertificatesUniqueMaxAmount) {
		List<OrderDto> orderDtosToCreate = new ArrayList<>(ordersAmount);
		for (int i = 0; i < ordersAmount; i++) {
			OrderDto orderDto = new OrderDto();
			int orderCertificatesAmount = DataGenerator.getRandomNumber(orderCertificatesUniqueMinAmount,
					orderCertificatesUniqueMaxAmount + 1);
			List<OrderCertificateDto> orderCertificateDtos = new ArrayList<>(orderCertificatesAmount);
			for (int j = orderCertificatesUniqueMinAmount; j <= orderCertificatesAmount; j++) {
				List<Integer> usedCertificateIndeces = new ArrayList<>(orderCertificatesAmount);
				int certificateIndex = DataGenerator.getRandomNumber(0, existedCertificates.size());
				while (usedCertificateIndeces.contains(certificateIndex)) {
					certificateIndex = DataGenerator.getRandomNumber(0, existedCertificates.size());
				}
				OrderCertificateDto orderCertificateDto = new OrderCertificateDto();
				orderCertificateDto.setCertificate(existedCertificates.get(certificateIndex));
				orderCertificateDto.setCertificateAmount(
						DataGenerator.getRandomNumber(orderCertificatesMinAmount, orderCertificatesMaxAmount + 1));
				orderCertificateDtos.add(orderCertificateDto);
				usedCertificateIndeces.add(certificateIndex);
			}
			orderDto.setUser(existedUsers.get(DataGenerator.getRandomNumber(0, existedUsers.size())));
			orderDto.setCertificates(orderCertificateDtos);
			orderDtosToCreate.add(orderDto);
		}
		return orderService.createOrders(orderDtosToCreate);
	}
}
