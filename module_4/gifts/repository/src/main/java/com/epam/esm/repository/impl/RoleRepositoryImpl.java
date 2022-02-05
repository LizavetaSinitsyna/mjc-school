package com.epam.esm.repository.impl;

import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import com.epam.esm.repository.RoleRepository;
import com.epam.esm.repository.model.RoleModel;
import com.epam.esm.repository.query_builder.RoleQueryBuilder;

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

	private final RoleQueryBuilder roleQueryBuilder;

	public RoleRepositoryImpl(RoleQueryBuilder roleQueryBuilder) {
		this.roleQueryBuilder = roleQueryBuilder;
	}

	/**
	 * Reads role with passed name.
	 * 
	 * @param roleName the name of the role to be read
	 * @return role with passed name
	 */
	@Override
	public Optional<RoleModel> findByName(String roleName) {
		try {
			return Optional.of(roleQueryBuilder.obtainReadByNameQuery(entityManager, roleName).getSingleResult());
		} catch (NoResultException e) {
			return Optional.empty();
		}
	}
}
