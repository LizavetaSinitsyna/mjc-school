package com.epam.esm.repository.query_builder;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Component;

import com.epam.esm.repository.model.UserModel_;
import com.epam.esm.repository.model.TagModel;
import com.epam.esm.repository.model.UserModel;

@Component
public class UserQueryBuilder {

	public UserQueryBuilder() {

	}

	public TypedQuery<UserModel> obtainReadByLoginQuery(EntityManager entityManager, String login) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<UserModel> userCriteria = criteriaBuilder.createQuery(UserModel.class);
		Root<UserModel> userRoot = userCriteria.from(UserModel.class);
		userCriteria.select(userRoot);
		userCriteria.where(
				criteriaBuilder.equal(criteriaBuilder.lower(userRoot.get(UserModel_.username)), login.toLowerCase()));
		return entityManager.createQuery(userCriteria);
	}

	public TypedQuery<UserModel> obtainReadAllQuery(EntityManager entityManager, int pageNumber, int limit) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<UserModel> userCriteria = criteriaBuilder.createQuery(UserModel.class);
		Root<UserModel> userRoot = userCriteria.from(UserModel.class);
		userCriteria.select(userRoot);
		TypedQuery<UserModel> typedQuery = entityManager.createQuery(userCriteria);
		typedQuery.setFirstResult(QueryBuilderUtil.retrieveStartIndex(pageNumber, limit));
		typedQuery.setMaxResults(limit);

		return typedQuery;
	}

	public TypedQuery<Long> obtainCounterQuery(EntityManager entityManager) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

		CriteriaQuery<Long> counterCriteria = criteriaBuilder.createQuery(Long.class);
		Root<UserModel> counterRoot = counterCriteria.from(UserModel.class);
		counterCriteria.select(criteriaBuilder.count(counterRoot));
		return entityManager.createQuery(counterCriteria);
	}
}
