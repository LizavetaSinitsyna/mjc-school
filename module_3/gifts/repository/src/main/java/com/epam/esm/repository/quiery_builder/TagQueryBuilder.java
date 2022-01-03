package com.epam.esm.repository.quiery_builder;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Component;

import com.epam.esm.repository.model.EntityConstant;
import com.epam.esm.repository.model.TagModel;
import com.epam.esm.repository.model.TagModel_;

@Component
public class TagQueryBuilder {

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
}
