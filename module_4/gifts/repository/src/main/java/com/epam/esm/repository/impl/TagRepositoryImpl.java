package com.epam.esm.repository.impl;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.epam.esm.repository.TagRepository;
import com.epam.esm.repository.model.TagModel;
import com.epam.esm.repository.query_builder.TagQueryBuilder;

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
		long totalEntriesAmount = tagQueryBuilder.obtainCounterQuery(entityManager).getSingleResult();
		List<TagModel> tags = tagQueryBuilder.obtainReadAllQuery(entityManager, pageNumber, limit).getResultList();

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
		return tagQueryBuilder.obtainDeleteQuery(entityManager, tagId).executeUpdate();
	}

	/**
	 * Restores deleted tag.
	 * 
	 * @param tagModel the tag to be restored
	 * @return restored tag
	 */
	@Override
	public int restore(long tagId) {
		return tagQueryBuilder.obtainRestoreQuery(entityManager, tagId).executeUpdate();
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
			return Optional.ofNullable((TagModel) tagQueryBuilder
					.obtainFindPopularTagByMostProfitableUserQuery(entityManager).getSingleResult());
		} catch (NoResultException e) {
			return Optional.empty();
		}
	}
}
