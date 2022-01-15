package com.epam.esm.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.util.MultiValueMap;

import com.epam.esm.dto.OrderDataDto;
import com.epam.esm.dto.OrderDto;

/**
 * 
 * Contains methods for working mostly with order entities.
 *
 */
public interface OrderService {
	/**
	 * Creates and saves the passed order for the specified user.
	 * 
	 * @param orderDto the order to be saved
	 * @param userId   the id of the user whose order will be saved
	 * @return saved order
	 */
	OrderDto create(long userId, OrderDto orderDto);

	/**
	 * Creates and saves the passed orders.
	 * 
	 * @param orderDtos the orders to be saved
	 * @return saved orders
	 */
	List<OrderDto> createOrders(List<OrderDto> orderDtos);

	/**
	 * Reads order with passed id.
	 * 
	 * @param orderId id of the order to be read
	 * @return order with passed id
	 */
	OrderDto readById(long orderId);

	/**
	 * Reads all orders for the specified user according to the passed parameters.
	 * 
	 * @param userId id of the user whose orders should be read
	 * @param params the parameters which define the choice of orders and their
	 *               ordering
	 * @return orders for specified user which meet the passed parameters
	 */
	Page<OrderDto> readAllByUserId(long userId, MultiValueMap<String, String> params);

	/**
	 * Reads information about the order with passed id for the specified user.
	 * 
	 * @param userId  id of the user whose order should be read
	 * @param orderId id of the order to be read
	 * @return information about the order with passed id for the specified user
	 */
	OrderDataDto readOrderDataByUserId(long userId, long orderId);

	/**
	 * Reads all orders according to the passed parameters.
	 * 
	 * @param params the parameters which define the choice of orders and their
	 *               ordering
	 * @return orders which meet passed parameters
	 */
	Page<OrderDto> readAll(MultiValueMap<String, String> params);
}
