package com.epam.esm.repository.impl;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.epam.esm.repository.OrderRepository;
import com.epam.esm.repository.model.CertificateModel;
import com.epam.esm.repository.model.OrderCertificateModel;
import com.epam.esm.repository.model.OrderModel;
import com.epam.esm.repository.model.UserModel;
import com.epam.esm.repository.query_builder.OrderQueryBuilder;

/**
 * 
 * Contains methods implementation for working mostly with {@code OrderModel}
 * entity.
 *
 */
@Repository
public class OrderRepositoryImpl implements OrderRepository {
	@PersistenceContext
	private EntityManager entityManager;

	@Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
	private int batchSize;

	private final OrderQueryBuilder orderQueryBuilder;

	public OrderRepositoryImpl(OrderQueryBuilder orderQueryBuilder) {
		this.orderQueryBuilder = orderQueryBuilder;
	}

	/**
	 * Saves the passed order.
	 * 
	 * @param orderModel the order to be saved
	 * @return saved order
	 */
	@Override
	@Transactional
	public OrderModel save(OrderModel orderModel) {
		prepareOrderModelToSave(orderModel);
		entityManager.persist(orderModel);
		return orderModel;
	}

	/**
	 * Saves the passed orders.
	 * 
	 * @param ordersToSave the orders to be saved
	 * @return saved orders
	 */
	@Override
	@Transactional
	public List<OrderModel> saveOrders(List<OrderModel> ordersToSave) {
		int i = 0;
		if (ordersToSave != null) {
			for (OrderModel orderModel : ordersToSave) {
				prepareOrderModelToSave(orderModel);
				entityManager.persist(orderModel);
				++i;
				if (i > 0 && i % batchSize == 0) {
					entityManager.flush();
					entityManager.clear();
				}
			}
		}
		return ordersToSave;
	}

	private void prepareOrderModelToSave(OrderModel orderModel) {
		List<OrderCertificateModel> orderCertificateModels = orderModel.getCertificates();
		if (orderCertificateModels != null && !orderCertificateModels.isEmpty()) {
			for (OrderCertificateModel orderCertificate : orderCertificateModels) {
				CertificateModel certificateModel = entityManager.find(CertificateModel.class,
						orderCertificate.getCertificate().getId());
				orderCertificate.setCertificate(certificateModel);
				orderCertificate.setOrder(orderModel);
			}
		}
		UserModel userModel = entityManager.find(UserModel.class, orderModel.getUser().getId());
		orderModel.setUser(userModel);
	}

	/**
	 * Reads order with passed id.
	 * 
	 * @param orderId the id of the order to be read
	 * @return order with passed id
	 */
	@Override
	public Optional<OrderModel> findById(long orderId) {
		return Optional.ofNullable(entityManager.find(OrderModel.class, orderId));
	}

	/**
	 * Reads all orders according to the passed parameters.
	 * 
	 * @param pageNumber start position for orders reading
	 * @param limit      amount of orders to be read
	 * @return orders which meet passed parameters
	 */
	@Override
	public Page<OrderModel> findAll(int pageNumber, int limit) {
		long totalEntriesAmount = orderQueryBuilder.obtainCounterQuery(entityManager).getSingleResult();
		List<OrderModel> orders = orderQueryBuilder.obtainReadAllQuery(entityManager, pageNumber, limit)
				.getResultList();

		Pageable pageable = PageRequest.of(pageNumber, limit);
		Page<OrderModel> pageModel = new PageImpl<>(orders, pageable, totalEntriesAmount);

		return pageModel;
	}

	/**
	 * Reads all orders for the specified user according to the passed parameters.
	 * 
	 * @param userId     id of the user whose orders should be read
	 * @param pageNumber start position for orders reading
	 * @param limit      amount of orders to be read
	 * @return orders which meet passed parameters
	 */
	@Override
	public Page<OrderModel> readAllByUserId(long userId, int pageNumber, int limit) {
		long totalEntriesAmount = orderQueryBuilder.obtainCounterByUserIdQuery(entityManager, userId).getSingleResult();
		List<OrderModel> orders = orderQueryBuilder.obtainReadAllByUserIdQuery(entityManager, pageNumber, limit, userId)
				.getResultList();

		Pageable pageable = PageRequest.of(pageNumber, limit);
		Page<OrderModel> pageModel = new PageImpl<>(orders, pageable, totalEntriesAmount);

		return pageModel;
	}
}
