package com.epam.esm.repository;

import java.util.List;

import com.epam.esm.repository.model.OrderModel;
import com.epam.esm.repository.model.PageModel;

/**
 * 
 * Contains methods for working mostly with {@code OrderModel} entity.
 *
 */
public interface OrderRepository extends GeneralRepository<OrderModel> {
	/**
	 * Reads all orders for the specified user according to the passed parameters.
	 * 
	 * @param userId id of the user whose orders should be read
	 * @param offset start position for orders reading
	 * @param limit  amount of orders to be read
	 * @return orders which meet passed parameters
	 */
	PageModel<OrderModel> readAllByUserId(long userId, int offset, int limit);

	/**
	 * Saves the passed orders.
	 * 
	 * @param ordersToSave the orders to be saved
	 * @return saved orders
	 */
	List<OrderModel> saveOrders(List<OrderModel> ordersToSave);

}
