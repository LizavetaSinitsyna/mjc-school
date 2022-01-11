package com.epam.esm.repository.impl;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

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

import org.springframework.stereotype.Repository;
import org.springframework.util.MultiValueMap;

import com.epam.esm.repository.RoleRepository;
import com.epam.esm.repository.model.EntityConstant;
import com.epam.esm.repository.model.RoleModel;
import com.epam.esm.repository.model.RoleModel_;

/**
 * 
 * Contains methods implementation for working mostly with {@code RoleModel}
 * entity.
 *
 */
@Repository
public class RoleRepositoryImpl implements RoleRepository {
	@PersistenceContext
	private EntityManager entityManager;

	public RoleRepositoryImpl() {

	}

	/**
	 * Reads role with passed name.
	 * 
	 * @param roleName the name of the role to be read
	 * @return role with passed name
	 */
	@Override
	public Optional<RoleModel> findByName(String roleName) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<RoleModel> roleCriteria = criteriaBuilder.createQuery(RoleModel.class);
		Root<RoleModel> roleRoot = roleCriteria.from(RoleModel.class);
		roleCriteria.select(roleRoot);
		roleCriteria.where(
				criteriaBuilder.equal(criteriaBuilder.lower(roleRoot.get(RoleModel_.name)), roleName.toLowerCase()));
		try {
			return Optional.of(entityManager.createQuery(roleCriteria).getSingleResult());
		} catch (NoResultException e) {
			return Optional.empty();
		}
	}
}
