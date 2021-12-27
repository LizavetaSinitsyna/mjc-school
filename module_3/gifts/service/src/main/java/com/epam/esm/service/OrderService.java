package com.epam.esm.service;

import java.util.List;

import org.springframework.util.MultiValueMap;

import com.epam.esm.dto.OrderDataDto;
import com.epam.esm.dto.OrderDto;

/**
 * 
 * Contains methods for working mostly with {@code OrderDto} entity.
 *
 */
public interface OrderService {
	/**
	 * Creates and saves the passed order.
	 * 
	 * @param orderDto the order to be saved
	 * @return saved order
	 */
	OrderDto create(long userId, OrderDto orderDto);

	/**
	 * Reads order with passed id.
	 * 
	 * @param orderId id of order to be read
	 * @return order with passed id
	 */
	OrderDto readById(long orderId);

	List<OrderDto> readAllByUserId(long userId, MultiValueMap<String, String> params);

	OrderDataDto readOrderDataByUserId(long userId, long orderId);

	List<OrderDto> readAll(MultiValueMap<String, String> params);

}
