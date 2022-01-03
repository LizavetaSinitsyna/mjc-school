package com.epam.esm.repository.impl;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.hibernate.Criteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.util.MultiValueMap;

import com.epam.esm.repository.OrderRepository;
import com.epam.esm.repository.TagRepository;
import com.epam.esm.repository.UserRepository;
import com.epam.esm.repository.model.CertificateModel;
import com.epam.esm.repository.model.OrderCertificateModel;
import com.epam.esm.repository.model.OrderModel;
import com.epam.esm.repository.model.OrderModel_;
import com.epam.esm.repository.model.EntityConstant;
import com.epam.esm.repository.model.TagModel;
import com.epam.esm.repository.model.UserModel;
import com.epam.esm.repository.model.UserModel_;

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

	public OrderRepositoryImpl() {

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
	 * @param offset start position for orders reading
	 * @param limit  amount of orders to be read
	 * @return orders which meet passed parameters
	 */
	@Override
	public List<OrderModel> findAll(int offset, int limit) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<OrderModel> orderCriteria = criteriaBuilder.createQuery(OrderModel.class);
		Root<OrderModel> orderRoot = orderCriteria.from(OrderModel.class);
		orderCriteria.select(orderRoot);
		TypedQuery<OrderModel> typedQuery = entityManager.createQuery(orderCriteria);
		typedQuery.setFirstResult(offset);
		typedQuery.setMaxResults(limit);
		return typedQuery.getResultList();
	}

	/**
	 * Reads all orders for the specified user according to the passed parameters.
	 * 
	 * @param userId id of the user whose orders should be read
	 * @param offset start position for orders reading
	 * @param limit  amount of orders to be read
	 * @return orders which meet passed parameters
	 */
	@Override
	public List<OrderModel> readAllByUserId(long userId, int offset, int limit) {
		List<OrderModel> orders = new ArrayList<>();
		UserModel userModel = entityManager.find(UserModel.class, userId);
		if (userModel != null) {
			orders = userModel.getOrders();
		}
		return orders;
	}
}
