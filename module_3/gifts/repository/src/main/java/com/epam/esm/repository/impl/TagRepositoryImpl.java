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
import com.epam.esm.repository.model.CertificateModel;
import com.epam.esm.repository.model.EntityConstant;
import com.epam.esm.repository.model.TagModel;
import com.epam.esm.repository.model.TagModel_;

/**
 * 
 * Contains methods implementation for working mostly with {@code TagModel}
 * entity.
 *
 */
@Repository
public class TagRepositoryImpl implements TagRepository {

	@PersistenceContext
	private EntityManager entityManager;

	public TagRepositoryImpl() {

	}

	/**
	 * Saves the passed tag.
	 * 
	 * @param tagModel the tag to be saved
	 * @return saved tag
	 */
	@Override
	@Transactional
	public TagModel save(TagModel tagModel) {
		entityManager.persist(tagModel);
		return tagModel;
	}

	/**
	 * Reads tag with passed id.
	 * 
	 * @param tagId the id of tag to be read
	 * @return tag with passed id
	 */

	@Override
	public Optional<TagModel> findById(long tagId) {
		try {
			return Optional.of(obtainReadByIdQuery(tagId).getSingleResult());
		} catch (NoResultException e) {
			return Optional.empty();
		}
	}

	/**
	 * Checks whether tag with passed id exists.
	 * 
	 * @param tagId the id of tag to be checked
	 * @return {@code true} if the the tag with passed id already exists and
	 *         {@code false} otherwise
	 */
	@Override
	public boolean tagExistsById(long tagId) {
		try {
			obtainReadByIdQuery(tagId).getSingleResult();
		} catch (NoResultException e) {
			return false;
		}
		return true;

	}

	private TypedQuery<TagModel> obtainReadByIdQuery(long tagId) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<TagModel> tagCriteria = criteriaBuilder.createQuery(TagModel.class);
		Root<TagModel> tagRoot = tagCriteria.from(TagModel.class);
		tagCriteria.select(tagRoot);

		Predicate isDeletedPredicate = criteriaBuilder.equal(tagRoot.get(TagModel_.isDeleted), false);
		Predicate idPredicate = criteriaBuilder.equal(tagRoot.get(TagModel_.id), tagId);
		tagCriteria.where(isDeletedPredicate, idPredicate);

		return entityManager.createQuery(tagCriteria);
	}

	/**
	 * Reads tag with passed name even if it is marked as deleted.
	 * 
	 * @param tagName the name of entity to be read
	 * @return tag with passed name
	 */

	@Override
	public Optional<TagModel> findByName(String tagName) {
		try {
			return Optional.of(obtainReadByNameQuery(tagName).getSingleResult());
		} catch (NoResultException e) {
			return Optional.empty();
		}
	}

	/**
	 * Checks whether tag with passed name exists.
	 * 
	 * @param tagName the name of tag to be checked
	 * @return {@code true} if the the tag with passed name already exists and
	 *         {@code false} otherwise
	 */
	@Override
	public boolean tagExistsByName(String tagName) {
		try {
			obtainReadByNameQuery(tagName).getSingleResult();
		} catch (NoResultException e) {
			return false;
		}
		return true;
	}

	private TypedQuery<TagModel> obtainReadByNameQuery(String tagName) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<TagModel> tagCriteria = criteriaBuilder.createQuery(TagModel.class);
		Root<TagModel> tagRoot = tagCriteria.from(TagModel.class);
		tagCriteria.select(tagRoot);
		tagCriteria.where(criteriaBuilder.equal(tagRoot.get(TagModel_.name), tagName));
		return entityManager.createQuery(tagCriteria);
	}

	/**
	 * Reads all tags according to passed parameters.
	 * 
	 * @param params the parameters which define choice of tags and their ordering
	 * @return tags which meet passed parameters
	 */
	@Override
	public List<TagModel> findAll(int offset, int limit) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<TagModel> tagCriteria = criteriaBuilder.createQuery(TagModel.class);
		Root<TagModel> tagRoot = tagCriteria.from(TagModel.class);
		tagCriteria.select(tagRoot);
		tagCriteria.where(criteriaBuilder.equal(tagRoot.get(TagModel_.isDeleted), false));
		TypedQuery<TagModel> typedQuery = entityManager.createQuery(tagCriteria);
		typedQuery.setFirstResult(offset);
		typedQuery.setMaxResults(limit);
		return typedQuery.getResultList();
	}

	/**
	 * Deletes tag with passed id.
	 * 
	 * @param tagId the id of tag to be deleted
	 * @return the number of deleted tags
	 */
	@Override
	public int delete(long tagId) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaUpdate<TagModel> tagCriteria = criteriaBuilder.createCriteriaUpdate(TagModel.class);
		Root<TagModel> tagRoot = tagCriteria.from(TagModel.class);
		tagCriteria.set(TagModel_.isDeleted, true);
		tagCriteria.where(criteriaBuilder.equal(tagRoot.get(TagModel_.id), tagId));
		return entityManager.createQuery(tagCriteria).executeUpdate();
	}

	/**
	 * Restores deleted tag.
	 * 
	 * @param tagModel the tag to be restored
	 * @return restored tag
	 */
	@Override
	public int restore(long tagId) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaUpdate<TagModel> tagCriteria = criteriaBuilder.createCriteriaUpdate(TagModel.class);
		Root<TagModel> tagRoot = tagCriteria.from(TagModel.class);
		tagCriteria.set(TagModel_.isDeleted, false);
		tagCriteria.where(criteriaBuilder.equal(tagRoot.get(TagModel_.id), tagId));
		return entityManager.createQuery(tagCriteria).executeUpdate();
	}
}
