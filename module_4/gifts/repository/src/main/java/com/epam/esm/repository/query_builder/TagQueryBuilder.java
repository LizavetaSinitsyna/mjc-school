package com.epam.esm.repository.query_builder;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import com.epam.esm.repository.model.CertificateModel;
import com.epam.esm.repository.model.EntityConstant;
import com.epam.esm.repository.model.TagModel;
import com.epam.esm.repository.model.TagModel_;

@Component
public class TagQueryBuilder {
	private static final String FIND_POPULAR_TAG_BY_MOST_PROFITABLE_USER = "SELECT tags.id, tags.name, tags.is_deleted, SUM(certificate_amount) AS tags_amount "
			+ "FROM tags INNER JOIN tags_certificates ON tags.id = tags_certificates.tag_id "
			+ "INNER JOIN gift_certificates ON tags_certificates.certificate_id = gift_certificates.id "
			+ "INNER JOIN orders_certificates ON gift_certificates.id = orders_certificates.certificate_id "
			+ "INNER JOIN orders ON orders_certificates.order_id = orders.id WHERE orders.user_id = "
			+ "(SELECT find_user_subquery.id FROM (SELECT users.id, SUM(cost) FROM users INNER JOIN orders"
			+ " ON users.id = orders.user_id GROUP BY users.id ORDER BY 2 DESC LIMIT 1) find_user_subquery) "
			+ "GROUP BY tags.id ORDER BY 4 DESC LIMIT 1";

	public TagQueryBuilder() {

	}

	public TypedQuery<TagModel> obtainReadByIdQuery(EntityManager entityManager, long tagId) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<TagModel> tagCriteria = criteriaBuilder.createQuery(TagModel.class);
		Root<TagModel> tagRoot = tagCriteria.from(TagModel.class);
		tagCriteria.select(tagRoot);

		Predicate isDeletedPredicate = criteriaBuilder.equal(tagRoot.get(TagModel_.isDeleted), false);
		Predicate idPredicate = criteriaBuilder.equal(tagRoot.get(TagModel_.id), tagId);
		tagCriteria.where(isDeletedPredicate, idPredicate);

		return entityManager.createQuery(tagCriteria);
	}

	public TypedQuery<TagModel> obtainReadByNameQuery(EntityManager entityManager, String tagName) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<TagModel> tagCriteria = criteriaBuilder.createQuery(TagModel.class);
		Root<TagModel> tagRoot = tagCriteria.from(TagModel.class);
		tagCriteria.select(tagRoot);
		tagCriteria.where(
				criteriaBuilder.equal(criteriaBuilder.lower(tagRoot.get(TagModel_.name)), tagName.toLowerCase()));
		return entityManager.createQuery(tagCriteria);
	}

	public Query obtainDeleteQuery(EntityManager entityManager, long tagId) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaUpdate<TagModel> tagCriteria = criteriaBuilder.createCriteriaUpdate(TagModel.class);
		Root<TagModel> tagRoot = tagCriteria.from(TagModel.class);
		tagCriteria.set(TagModel_.isDeleted, true);
		tagCriteria.where(criteriaBuilder.equal(tagRoot.get(TagModel_.id), tagId));
		return entityManager.createQuery(tagCriteria);
	}

	public Query obtainRestoreQuery(EntityManager entityManager, long tagId) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaUpdate<TagModel> tagCriteria = criteriaBuilder.createCriteriaUpdate(TagModel.class);
		Root<TagModel> tagRoot = tagCriteria.from(TagModel.class);
		tagCriteria.set(TagModel_.isDeleted, false);
		tagCriteria.where(criteriaBuilder.equal(tagRoot.get(TagModel_.id), tagId));
		return entityManager.createQuery(tagCriteria);
	}

	public TypedQuery<TagModel> obtainReadAllQuery(EntityManager entityManager, int pageNumber, int limit) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<TagModel> tagCriteria = criteriaBuilder.createQuery(TagModel.class);
		Root<TagModel> tagRoot = tagCriteria.from(TagModel.class);
		tagCriteria.select(tagRoot);
		tagCriteria.where(obtainDeletePredicate(criteriaBuilder, tagRoot));

		TypedQuery<TagModel> typedQuery = entityManager.createQuery(tagCriteria);
		typedQuery.setFirstResult(QueryBuilderUtil.retrieveStartIndex(pageNumber, limit));
		typedQuery.setMaxResults(limit);

		return typedQuery;
	}

	public TypedQuery<Long> obtainCounterQuery(EntityManager entityManager) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> counterCriteria = criteriaBuilder.createQuery(Long.class);
		Root<TagModel> counterRoot = counterCriteria.from(TagModel.class);
		counterCriteria.select(criteriaBuilder.count(counterRoot));
		counterCriteria.where(obtainDeletePredicate(criteriaBuilder, counterRoot));
		return entityManager.createQuery(counterCriteria);
	}

	private Predicate obtainDeletePredicate(CriteriaBuilder criteriaBuilder, Root<TagModel> tagRoot) {
		return criteriaBuilder.equal(tagRoot.get(TagModel_.isDeleted), false);
	}

	public Query obtainFindPopularTagByMostProfitableUserQuery(EntityManager entityManager) {
		return entityManager.createNativeQuery(FIND_POPULAR_TAG_BY_MOST_PROFITABLE_USER, TagModel.class);
	}
}
