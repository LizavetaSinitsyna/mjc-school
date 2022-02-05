package com.epam.esm.repository.query_builder;

import java.util.Optional;

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
import com.epam.esm.repository.model.RoleModel;
import com.epam.esm.repository.model.RoleModel_;

@Component
public class RoleQueryBuilder {

	public RoleQueryBuilder() {

	}

	public TypedQuery<RoleModel> obtainReadByNameQuery(EntityManager entityManager, String roleName) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<RoleModel> roleCriteria = criteriaBuilder.createQuery(RoleModel.class);
		Root<RoleModel> roleRoot = roleCriteria.from(RoleModel.class);
		roleCriteria.select(roleRoot);
		roleCriteria.where(
				criteriaBuilder.equal(criteriaBuilder.lower(roleRoot.get(RoleModel_.name)), roleName.toLowerCase()));
		return entityManager.createQuery(roleCriteria);
	}
}
