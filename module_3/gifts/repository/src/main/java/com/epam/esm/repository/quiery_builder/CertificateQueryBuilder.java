package com.epam.esm.repository.quiery_builder;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
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

	public TypedQuery<CertificateModel> obtainReadByIdQuery(EntityManager entityManager, long tagId) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<CertificateModel> certificateCriteria = criteriaBuilder.createQuery(CertificateModel.class);
		Root<CertificateModel> certificateRoot = certificateCriteria.from(CertificateModel.class);
		certificateCriteria.select(certificateRoot);

		Predicate isDeletedPredicate = criteriaBuilder.equal(certificateRoot.get(CertificateModel_.isDeleted), false);
		Predicate idPredicate = criteriaBuilder.equal(certificateRoot.get(CertificateModel_.id), tagId);
		certificateCriteria.where(isDeletedPredicate, idPredicate);

		return entityManager.createQuery(certificateCriteria);
	}

	public TypedQuery<CertificateModel> obtainReadByNameQuery(EntityManager entityManager, String certificateName) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<CertificateModel> certificateCriteria = criteriaBuilder.createQuery(CertificateModel.class);
		Root<CertificateModel> certificateRoot = certificateCriteria.from(CertificateModel.class);
		certificateCriteria.select(certificateRoot);

		Predicate isDeletedPredicate = criteriaBuilder.equal(certificateRoot.get(CertificateModel_.isDeleted), false);
		Predicate namePredicate = criteriaBuilder.equal(
				criteriaBuilder.lower(certificateRoot.get(CertificateModel_.name)), certificateName.toLowerCase());
		certificateCriteria.where(isDeletedPredicate, namePredicate);

		return entityManager.createQuery(certificateCriteria);
	}

	public Predicate[] obtainPredicates(MultiValueMap<String, String> params, CriteriaBuilder criteriaBuilder,
			Root<CertificateModel> certificateRoot) {
		List<Predicate> predicates = new ArrayList<>();

		List<String> tags = params.get(EntityConstant.TAG);
		if (tags != null && !tags.isEmpty()) {
			Join<CertificateModel, TagModel> join = certificateRoot.join(CertificateModel_.tags, JoinType.INNER);
			for (String tag : tags) {
				predicates
						.add(criteriaBuilder.equal(criteriaBuilder.lower(join.get(TagModel_.name)), tag.toLowerCase()));
			}
		}

		List<String> searchPart = params.get(EntityConstant.SEARCH);
		if (searchPart != null) {
			String search = StringUtils.wrap(searchPart.get(0), PROCENT);
			Predicate nameSearchPredicate = criteriaBuilder
					.like(criteriaBuilder.lower(certificateRoot.get(CertificateModel_.name)), search.toLowerCase());
			Predicate descriptionSearchPredicate = criteriaBuilder.like(
					criteriaBuilder.lower(certificateRoot.get(CertificateModel_.description)), search.toLowerCase());
			predicates.add(criteriaBuilder.or(nameSearchPredicate, descriptionSearchPredicate));
		}

		predicates.add(criteriaBuilder.equal(certificateRoot.get(CertificateModel_.isDeleted), false));

		Predicate[] result = new Predicate[predicates.size()];
		return predicates.toArray(result);
	}

	public List<Order> obtainOrders(MultiValueMap<String, String> params, CriteriaBuilder criteriaBuilder,
			Root<CertificateModel> certificateRoot) {
		List<Order> orderConditions = new ArrayList<>();
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
		return orderConditions;
	}
}
