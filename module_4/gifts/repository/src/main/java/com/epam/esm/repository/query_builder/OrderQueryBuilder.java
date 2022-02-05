package com.epam.esm.repository.query_builder;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Component;

import com.epam.esm.repository.model.CertificateModel;
import com.epam.esm.repository.model.OrderModel;
import com.epam.esm.repository.model.OrderModel_;
import com.epam.esm.repository.model.UserModel_;

@Component
public class OrderQueryBuilder {

	public OrderQueryBuilder() {

	}

	public TypedQuery<OrderModel> obtainReadAllQuery(EntityManager entityManager, int pageNumber, int limit) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<OrderModel> orderCriteria = criteriaBuilder.createQuery(OrderModel.class);
		Root<OrderModel> orderRoot = orderCriteria.from(OrderModel.class);
		orderCriteria.select(orderRoot);

		TypedQuery<OrderModel> typedQuery = entityManager.createQuery(orderCriteria);
		typedQuery.setFirstResult(QueryBuilderUtil.retrieveStartIndex(pageNumber, limit));
		typedQuery.setMaxResults(limit);

		return typedQuery;
	}

	public TypedQuery<Long> obtainCounterQuery(EntityManager entityManager) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> counterCriteria = criteriaBuilder.createQuery(Long.class);
		Root<OrderModel> counterRoot = counterCriteria.from(OrderModel.class);
		counterCriteria.select(criteriaBuilder.count(counterRoot));
		return entityManager.createQuery(counterCriteria);
	}

	public TypedQuery<Long> obtainCounterByUserIdQuery(EntityManager entityManager, long userId) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> counterCriteria = criteriaBuilder.createQuery(Long.class);
		Root<OrderModel> counterRoot = counterCriteria.from(OrderModel.class);
		counterCriteria.select(criteriaBuilder.count(counterRoot));
		counterCriteria.where(obtainUserIdPredicate(criteriaBuilder, counterRoot, userId));
		return entityManager.createQuery(counterCriteria);
	}

	public TypedQuery<OrderModel> obtainReadAllByUserIdQuery(EntityManager entityManager, int pageNumber, int limit,
			long userId) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<OrderModel> orderCriteria = criteriaBuilder.createQuery(OrderModel.class);
		Root<OrderModel> orderRoot = orderCriteria.from(OrderModel.class);
		orderCriteria.select(orderRoot);
		orderCriteria.where(obtainUserIdPredicate(criteriaBuilder, orderRoot, userId));

		TypedQuery<OrderModel> typedQuery = entityManager.createQuery(orderCriteria);
		typedQuery.setFirstResult(QueryBuilderUtil.retrieveStartIndex(pageNumber, limit));
		typedQuery.setMaxResults(limit);

		return typedQuery;
	}

	private Predicate obtainUserIdPredicate(CriteriaBuilder criteriaBuilder, Root<OrderModel> orderRoot, long userId) {
		return criteriaBuilder.equal(orderRoot.join(OrderModel_.user, JoinType.INNER).get(UserModel_.id), userId);
	}
}
