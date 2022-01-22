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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.MultiValueMap;

import com.epam.esm.repository.TagRepository;
import com.epam.esm.repository.model.EntityConstant;
import com.epam.esm.repository.model.TagModel;
import com.epam.esm.repository.model.TagModel_;
import com.epam.esm.repository.query_builder.QueryBuilderUtil;
import com.epam.esm.repository.query_builder.TagQueryBuilder;

/**
 * 
 * Contains methods implementation for working mostly with {@code TagModel}
 * entity.
 *
 */
@Repository
public class TagRepositoryImpl implements TagRepository {
	private static final String FIND_POPULAR_TAG_BY_MOST_PROFITABLE_USER = "SELECT tags.id, tags.name, tags.is_deleted, SUM(certificate_amount) AS tags_amount "
			+ "FROM tags INNER JOIN tags_certificates ON tags.id = tags_certificates.tag_id "
			+ "INNER JOIN gift_certificates ON tags_certificates.certificate_id = gift_certificates.id "
			+ "INNER JOIN orders_certificates ON gift_certificates.id = orders_certificates.certificate_id "
			+ "INNER JOIN orders ON orders_certificates.order_id = orders.id WHERE orders.user_id = "
			+ "(SELECT find_user_subquery.id FROM (SELECT users.id, SUM(cost) FROM users INNER JOIN orders"
			+ " ON users.id = orders.user_id GROUP BY users.id ORDER BY 2 DESC LIMIT 1) find_user_subquery) "
			+ "GROUP BY tags.id ORDER BY 4 DESC LIMIT 1";

	@PersistenceContext
	private EntityManager entityManager;

	@Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
	private int batchSize;

	private final TagQueryBuilder tagQueryBuilder;

	@Autowired
	public TagRepositoryImpl(TagQueryBuilder tagQueryBuilder) {
		this.tagQueryBuilder = tagQueryBuilder;
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
	 * Saves the passed tags.
	 * 
	 * @param tagModels the tags to be saved
	 * @return saved tags
	 */
	@Override
	@Transactional
	public List<TagModel> saveTags(List<TagModel> tagModels) {
		int i = 0;
		if (tagModels != null) {
			for (TagModel tagModel : tagModels) {
				entityManager.persist(tagModel);
				++i;
				if (i > 0 && i % batchSize == 0) {
					entityManager.flush();
					entityManager.clear();
				}
			}
		}
		return tagModels;
	}

	/**
	 * Reads tag with passed id.
	 * 
	 * @param tagId the id of the tag to be read
	 * @return tag with passed id
	 */
	@Override
	public Optional<TagModel> findById(long tagId) {
		try {
			return Optional.of(tagQueryBuilder.obtainReadByIdQuery(entityManager, tagId).getSingleResult());
		} catch (NoResultException e) {
			return Optional.empty();
		}
	}

	/**
	 * Checks whether the tag with passed id exists.
	 * 
	 * @param tagId the id of the tag to be checked
	 * @return {@code true} if the the tag with passed id already exists and
	 *         {@code false} otherwise
	 */
	@Override
	public boolean tagExistsById(long tagId) {
		try {
			tagQueryBuilder.obtainReadByIdQuery(entityManager, tagId).getSingleResult();
		} catch (NoResultException e) {
			return false;
		}
		return true;

	}

	/**
	 * Reads tag with passed name even if it is marked as deleted.
	 * 
	 * @param tagName the name of the tag to be read
	 * @return tag with passed name
	 */
	@Override
	public Optional<TagModel> findByName(String tagName) {
		try {
			return Optional.of(tagQueryBuilder.obtainReadByNameQuery(entityManager, tagName).getSingleResult());
		} catch (NoResultException e) {
			return Optional.empty();
		}
	}

	/**
	 * Checks whether the tag with passed name exists.
	 * 
	 * @param tagName the name of the tag to be checked
	 * @return {@code true} if the the tag with passed name already exists and
	 *         {@code false} otherwise
	 */
	@Override
	public boolean tagExistsByName(String tagName) {
		try {
			tagQueryBuilder.obtainReadByNameQuery(entityManager, tagName).getSingleResult();
		} catch (NoResultException e) {
			return false;
		}
		return true;
	}

	/**
	 * Reads all tags according to the passed parameters.
	 * 
	 * @param pageNumber start position for tags reading
	 * @param limit      amount of tags to be read
	 * @return tags which meet passed parameters
	 */
	@Override
	public Page<TagModel> findAll(int pageNumber, int limit) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

		CriteriaQuery<Long> counterCriteria = criteriaBuilder.createQuery(Long.class);
		Root<TagModel> counterRoot = counterCriteria.from(TagModel.class);
		counterCriteria.select(criteriaBuilder.count(counterRoot));
		counterCriteria.where(criteriaBuilder.equal(counterRoot.get(TagModel_.isDeleted), false));
		long totalEntriesAmount = entityManager.createQuery(counterCriteria).getSingleResult();

		CriteriaQuery<TagModel> tagCriteria = criteriaBuilder.createQuery(TagModel.class);
		Root<TagModel> tagRoot = tagCriteria.from(TagModel.class);
		tagCriteria.select(tagRoot);
		tagCriteria.where(criteriaBuilder.equal(tagRoot.get(TagModel_.isDeleted), false));
		TypedQuery<TagModel> typedQuery = entityManager.createQuery(tagCriteria);
		typedQuery.setFirstResult(QueryBuilderUtil.retrieveStartIndex(pageNumber, limit));
		typedQuery.setMaxResults(limit);
		List<TagModel> tags = typedQuery.getResultList();

		Pageable pageable = PageRequest.of(pageNumber, limit);
		Page<TagModel> pageModel = new PageImpl<>(tags, pageable, totalEntriesAmount);

		return pageModel;
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

	/**
	 * Finds the most widely used tag of a user with the highest cost of all orders.
	 * 
	 * @return the most widely used tag of a user with the highest cost of all
	 *         orders
	 */
	@Override
	public Optional<TagModel> findPopularTagByMostProfitableUser() {
		try {
			return Optional.ofNullable((TagModel) entityManager
					.createNativeQuery(FIND_POPULAR_TAG_BY_MOST_PROFITABLE_USER, TagModel.class).getSingleResult());
		} catch (NoResultException e) {
			return Optional.empty();
		}
	}
}
