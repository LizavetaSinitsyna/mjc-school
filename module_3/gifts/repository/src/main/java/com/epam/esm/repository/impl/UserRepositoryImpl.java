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

import org.hibernate.Criteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.util.MultiValueMap;

import com.epam.esm.repository.TagRepository;
import com.epam.esm.repository.UserRepository;
import com.epam.esm.repository.model.CertificateModel;
import com.epam.esm.repository.model.EntityConstant;
import com.epam.esm.repository.model.OrderModel;
import com.epam.esm.repository.model.TagModel;
import com.epam.esm.repository.model.UserModel;
import com.epam.esm.repository.model.UserModel_;

/**
 * 
 * Contains methods implementation for working mostly with {@code UserModel}
 * entity.
 *
 */
@Repository
public class UserRepositoryImpl implements UserRepository {

	@PersistenceContext
	private EntityManager entityManager;

	public UserRepositoryImpl() {

	}

	/**
	 * Saves the passed user.
	 * 
	 * @param userModel the user to be saved
	 * @return saved user
	 */
	@Override
	public UserModel save(UserModel userModel) {
		entityManager.persist(userModel);
		return userModel;
	}

	/**
	 * Reads user with passed id.
	 * 
	 * @param userId the id of user to be read
	 * @return user with passed id
	 */

	@Override
	public Optional<UserModel> findById(long userId) {
		return Optional.ofNullable(entityManager.find(UserModel.class, userId));
	}
	
	/**
	 * Checks whether user with passed id exists.
	 * 
	 * @param userId the id of user to be checked
	 * @return {@code true} if the the user with passed id already exists and
	 *         {@code false} otherwise
	 */
	@Override
	public boolean userExistsById(long userId) {
		UserModel userModel = entityManager.find(UserModel.class, userId);
		return userModel != null;

	}

	/**
	 * Reads user with passed name.
	 * 
	 * @param userName the name of the user to be read
	 * @return user with passed name
	 */

	@Override
	public Optional<UserModel> findByLogin(String login) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<UserModel> userCriteria = criteriaBuilder.createQuery(UserModel.class);
		Root<UserModel> userRoot = userCriteria.from(UserModel.class);
		userCriteria.select(userRoot);
		userCriteria.where(criteriaBuilder.equal(userRoot.get(UserModel_.login), login));
		try {
			return Optional.of(entityManager.createQuery(userCriteria).getSingleResult());
		} catch (NoResultException e) {
			return Optional.empty();
		}

	}

	/**
	 * Reads all users according to passed parameters.
	 * 
	 * @param params the parameters which define choice of users and their ordering
	 * @return users which meet passed parameters
	 */
	@Override
	public List<UserModel> findAll(int offset, int limit) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<UserModel> userCriteria = criteriaBuilder.createQuery(UserModel.class);
		Root<UserModel> userRoot = userCriteria.from(UserModel.class);
		userCriteria.select(userRoot);
		TypedQuery<UserModel> typedQuery = entityManager.createQuery(userCriteria);
		typedQuery.setFirstResult(offset);
		typedQuery.setMaxResults(limit);
		return typedQuery.getResultList();
	}
}
