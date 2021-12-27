package com.epam.esm.repository;

import java.util.List;

import com.epam.esm.repository.model.OrderModel;

/**
 * 
 * Contains methods for working mostly with {@code OrderModel} entity.
 *
 */
public interface OrderRepository extends GeneralRepository<OrderModel> {

	List<OrderModel> readAllByUserId(long userId, int offset, int limit);


}
