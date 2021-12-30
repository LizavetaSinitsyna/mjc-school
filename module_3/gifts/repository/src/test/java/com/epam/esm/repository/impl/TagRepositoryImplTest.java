package com.epam.esm.repository.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Root;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.epam.esm.repository.TagRepository;
import com.epam.esm.repository.model.TagModel;
import com.epam.esm.repository.model.TagModel_;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@EntityScan("com.epam.esm")
@ComponentScan("com.epam.esm")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class TagRepositoryImplTest {

	private static final Long TAG_ID_1 = 1L;
	private static final int OFFSET = 0;
	private static final int LIMIT_1 = 1;
	private static final int UPDATED_TAGS_AMOUNT = 1;
	private static final String TAG_NAME = "Family";
	private TagModel tag1;
	private TagModel tag2;
	private TagModel restoredTag1;

	@Autowired
	private TagRepository tagRepository;
	@PersistenceContext
	private EntityManager entityManager;

	@BeforeEach
	public void setUp() {
		tag1 = new TagModel();
		tag1.setName("food");

		tag2 = new TagModel();
		tag2.setName("family");

		restoredTag1 = new TagModel();
		restoredTag1.setName("food");
	}

	@Test
	void testCreate() {
		TagModel actual = tagRepository.save(tag1);
		Assertions.assertEquals(tag1, actual);
	}

	@Test
	void testReadById() {
		entityManager.persist(tag1);
		Optional<TagModel> actual = tagRepository.findById(TAG_ID_1);
		Assertions.assertEquals(Optional.of(tag1), actual);
	}

	@Test
	void testTagExistsById() {
		entityManager.persist(tag1);
		boolean actual = tagRepository.tagExistsById(TAG_ID_1);
		Assertions.assertTrue(actual);
	}

	@Test
	void testReadByName() {
		entityManager.persist(tag2);
		Optional<TagModel> actual = tagRepository.findByName(TAG_NAME);
		Assertions.assertEquals(Optional.of(tag2), actual);
	}

	@Test
	void testTagExistsByName() {
		entityManager.persist(tag2);
		boolean actual = tagRepository.tagExistsByName(TAG_NAME);
		Assertions.assertTrue(actual);
	}

	@Test
	void testReadAll() {
		entityManager.persist(tag1);
		entityManager.persist(tag2);
		List<TagModel> actual = tagRepository.findAll(OFFSET, LIMIT_1);
		List<TagModel> expected = Arrays.asList(tag1);
		Assertions.assertEquals(expected, actual);
	}

	@Test
	void testDelete() {
		entityManager.persist(tag1);
		int actual = tagRepository.delete(TAG_ID_1);
		Assertions.assertEquals(UPDATED_TAGS_AMOUNT, actual);
	}

	@Test
	void testRestore() {
		entityManager.persist(tag1);
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaUpdate<TagModel> tagCriteria = criteriaBuilder.createCriteriaUpdate(TagModel.class);
		Root<TagModel> tagRoot = tagCriteria.from(TagModel.class);
		tagCriteria.set(TagModel_.isDeleted, true);
		tagCriteria.where(criteriaBuilder.equal(tagRoot.get(TagModel_.id), TAG_ID_1));
		entityManager.createQuery(tagCriteria).executeUpdate();
		int actual = tagRepository.restore(TAG_ID_1);
		Assertions.assertEquals(UPDATED_TAGS_AMOUNT, actual);
	}
	
	@Test
	void testFindPopularTagByMostProfitableUser() {
		Optional<TagModel> actual = tagRepository.findPopularTagByMostProfitableUser();
		Assertions.assertTrue(actual.isEmpty());
	}

}
