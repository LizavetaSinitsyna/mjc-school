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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.util.MultiValueMap;

import com.epam.esm.repository.TagRepository;
import com.epam.esm.repository.UserRepository;
import com.epam.esm.repository.model.CertificateModel;
import com.epam.esm.repository.model.OrderModel;
import com.epam.esm.repository.model.RoleModel;
import com.epam.esm.repository.model.EntityConstant;
import com.epam.esm.repository.model.TagModel;
import com.epam.esm.repository.model.UserModel;
import com.epam.esm.repository.model.UserModel_;
import com.epam.esm.repository.quiery_builder.UserQueryBuilder;

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

	@Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
	private int batchSize;

	private UserQueryBuilder userQueryBuilder;

	@Autowired
	public UserRepositoryImpl(UserQueryBuilder userQueryBuilder) {
		this.userQueryBuilder = userQueryBuilder;
	}

	/**
	 * Saves the passed user.
	 * 
	 * @param userModel the user to be saved
	 * @return saved user
	 */
	@Override
	@Transactional
	public UserModel save(UserModel userModel) {
		entityManager.persist(userModel);
		return userModel;
	}

	/**
	 * Saves the passed users.
	 * 
	 * @param userModels the users to be saved
	 * @return saved users
	 */
	@Override
	@Transactional
	public List<UserModel> saveUsers(List<UserModel> userModels) {
		int i = 0;
		if (userModels != null) {
			for (UserModel userModel : userModels) {
				entityManager.persist(userModel);
				++i;
				if (i > 0 && i % batchSize == 0) {
					entityManager.flush();
					entityManager.clear();
				}
			}
		}
		return userModels;
	}

	/**
	 * Reads user with passed id.
	 * 
	 * @param userId the id of the user to be read
	 * @return user with passed id
	 */
	@Override
	public Optional<UserModel> findById(long userId) {
		return Optional.ofNullable(entityManager.find(UserModel.class, userId));
	}

	/**
	 * Checks whether the user with passed id exists.
	 * 
	 * @param userId the id of the user to be checked
	 * @return {@code true} if the the user with passed id already exists and
	 *         {@code false} otherwise
	 */
	@Override
	public boolean userExistsById(long userId) {
		UserModel userModel = entityManager.find(UserModel.class, userId);
		return userModel != null;
	}

	/**
	 * Reads user with passed login.
	 * 
	 * @param login the name of the user to be read
	 * @return user with passed login
	 */
	@Override
	public Optional<UserModel> findByLogin(String login) {
		try {
			return Optional.of(userQueryBuilder.obtainReadByLoginQuery(entityManager, login).getSingleResult());
		} catch (NoResultException e) {
			return Optional.empty();
		}
	}

	/**
	 * Checks whether user with passed login exists.
	 * 
	 * @param login the login of the user to be checked
	 * @return {@code true} if the the user with passed login already exists and
	 *         {@code false} otherwise
	 */
	@Override
	public boolean userExistsByLogin(String login) {
		try {
			userQueryBuilder.obtainReadByLoginQuery(entityManager, login).getSingleResult();
		} catch (NoResultException e) {
			return false;
		}
		return true;
	}

	/**
	 * Reads all users according to passed parameters.
	 * 
	 * @param offset start position for users reading
	 * @param limit  amount of users to be read
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
