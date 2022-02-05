package com.epam.esm.repository.query_builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import com.epam.esm.repository.model.CertificateModel;
import com.epam.esm.repository.model.CertificateModel_;
import com.epam.esm.repository.model.EntityConstant;
import com.epam.esm.repository.model.TagModel;
import com.epam.esm.repository.model.TagModel_;

@Component
public class CertificateQueryBuilder {
	private static final String PROCENT = "%";

	public CertificateQueryBuilder() {

	}

	public TypedQuery<CertificateModel> obtainReadByIdQuery(EntityManager entityManager, long certificateId) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<CertificateModel> certificateCriteria = criteriaBuilder.createQuery(CertificateModel.class);
		Root<CertificateModel> certificateRoot = certificateCriteria.from(CertificateModel.class);
		certificateCriteria.select(certificateRoot);

		Predicate idPredicate = criteriaBuilder.equal(certificateRoot.get(CertificateModel_.id), certificateId);
		certificateCriteria.where(obtainDeletePredicate(criteriaBuilder, certificateRoot), idPredicate);

		return entityManager.createQuery(certificateCriteria);
	}

	public TypedQuery<CertificateModel> obtainReadByTagIdQuery(EntityManager entityManager, long tagId) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<CertificateModel> certificateCriteria = criteriaBuilder.createQuery(CertificateModel.class);
		Root<CertificateModel> certificateRoot = certificateCriteria.from(CertificateModel.class);
		certificateCriteria.select(certificateRoot);

		Join<CertificateModel, TagModel> join = certificateRoot.join(CertificateModel_.tags, JoinType.INNER);
		certificateCriteria.where(criteriaBuilder.equal(join.get(TagModel_.id), tagId));

		return entityManager.createQuery(certificateCriteria);
	}

	public Query obtainDeleteQuery(EntityManager entityManager, long certificateId) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaUpdate<CertificateModel> certificateCriteria = criteriaBuilder
				.createCriteriaUpdate(CertificateModel.class);
		Root<CertificateModel> certificateRoot = certificateCriteria.from(CertificateModel.class);
		certificateCriteria.set(CertificateModel_.isDeleted, true);
		certificateCriteria.where(criteriaBuilder.equal(certificateRoot.get(CertificateModel_.id), certificateId));
		return entityManager.createQuery(certificateCriteria);
	}

	public TypedQuery<CertificateModel> obtainReadAllQuery(EntityManager entityManager,
			MultiValueMap<String, String> params, int pageNumber, int limit) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<CertificateModel> certificateCriteria = criteriaBuilder.createQuery(CertificateModel.class);
		Root<CertificateModel> certificateRoot = certificateCriteria.from(CertificateModel.class);
		certificateCriteria.select(certificateRoot);
		List<Predicate> predicates = obtainCustomPredicates(params, criteriaBuilder, certificateRoot);
		predicates.add(obtainDeletePredicate(criteriaBuilder, certificateRoot));
		certificateCriteria.where(predicates.toArray(new Predicate[predicates.size()]));
		certificateCriteria.orderBy(obtainOrders(params, criteriaBuilder, certificateRoot));

		TypedQuery<CertificateModel> typedQuery = entityManager.createQuery(certificateCriteria);
		typedQuery.setFirstResult(QueryBuilderUtil.retrieveStartIndex(pageNumber, limit));
		typedQuery.setMaxResults(limit);

		return typedQuery;
	}

	public TypedQuery<Long> obtainCounterQuery(MultiValueMap<String, String> params, EntityManager entityManager) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> counterCriteria = criteriaBuilder.createQuery(Long.class);
		Root<CertificateModel> counterRoot = counterCriteria.from(CertificateModel.class);
		counterCriteria.select(criteriaBuilder.count(counterRoot));

		List<Predicate> predicates = obtainCustomPredicates(params, criteriaBuilder, counterRoot);
		predicates.add(obtainDeletePredicate(criteriaBuilder, counterRoot));
		counterCriteria.where(predicates.toArray(new Predicate[predicates.size()]));
		return entityManager.createQuery(counterCriteria);
	}

	public TypedQuery<CertificateModel> obtainReadByNameQuery(EntityManager entityManager, String certificateName) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<CertificateModel> certificateCriteria = criteriaBuilder.createQuery(CertificateModel.class);
		Root<CertificateModel> certificateRoot = certificateCriteria.from(CertificateModel.class);
		certificateCriteria.select(certificateRoot);

		Predicate namePredicate = criteriaBuilder.equal(
				criteriaBuilder.lower(certificateRoot.get(CertificateModel_.name)), certificateName.toLowerCase());
		certificateCriteria.where(obtainDeletePredicate(criteriaBuilder, certificateRoot), namePredicate);

		return entityManager.createQuery(certificateCriteria);
	}

	private List<Predicate> obtainCustomPredicates(MultiValueMap<String, String> params,
			CriteriaBuilder criteriaBuilder, Root<CertificateModel> certificateRoot) {
		List<Predicate> predicates = new ArrayList<>();

		if (params != null) {
			List<String> tags = params.get(EntityConstant.TAG);
			if (tags != null && !tags.isEmpty()) {
				for (String tag : tags) {
					Join<CertificateModel, TagModel> join = certificateRoot.join(CertificateModel_.tags,
							JoinType.INNER);
					predicates.add(
							criteriaBuilder.equal(criteriaBuilder.lower(join.get(TagModel_.name)), tag.toLowerCase()));
				}
			}

			List<String> searchPart = params.get(EntityConstant.SEARCH);
			if (searchPart != null) {
				String search = StringUtils.wrap(searchPart.get(0), PROCENT);
				Predicate nameSearchPredicate = criteriaBuilder
						.like(criteriaBuilder.lower(certificateRoot.get(CertificateModel_.name)), search.toLowerCase());
				Predicate descriptionSearchPredicate = criteriaBuilder.like(
						criteriaBuilder.lower(certificateRoot.get(CertificateModel_.description)),
						search.toLowerCase());
				predicates.add(criteriaBuilder.or(nameSearchPredicate, descriptionSearchPredicate));
			}
		}

		return predicates;
	}

	private Predicate obtainDeletePredicate(CriteriaBuilder criteriaBuilder, Root<CertificateModel> certificateRoot) {
		return criteriaBuilder.equal(certificateRoot.get(CertificateModel_.isDeleted), false);
	}

	private List<Order> obtainOrders(MultiValueMap<String, String> params, CriteriaBuilder criteriaBuilder,
			Root<CertificateModel> certificateRoot) {
		List<Order> orderConditions = new ArrayList<>();
		if (params != null) {
			List<String> sortConditions = params.get(EntityConstant.ORDER_BY);
			if (sortConditions != null) {
				for (String sortParam : sortConditions) {
					int lastCharIndex = sortParam.length() - 1;
					if (sortParam.charAt(lastCharIndex) == EntityConstant.DESC_SIGN) {
						orderConditions
								.add(criteriaBuilder.desc(certificateRoot.get(sortParam.substring(0, lastCharIndex))));
					} else {
						orderConditions.add(criteriaBuilder.asc(certificateRoot.get(sortParam)));
					}
				}
			}
		}
		return orderConditions;
	}
}
