package com.epam.esm.repository.impl;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.epam.esm.repository.TagRepository;
import com.epam.esm.repository.config.JdbcTestConfiguration;
import com.epam.esm.repository.model.TagModel;

@SpringJUnitConfig(JdbcTestConfiguration.class)
@SqlGroup({ @Sql(scripts = "/dropTables.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD),
		@Sql(scripts = "/schema.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD),
		@Sql(scripts = "/data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD) })
class TagRepositoryImplTest {
	
	private static final Long TAG_ID_1 = 1L;
	private static final Long TAG_ID_2 = 2L;
	private static final Long TAG_ID_3 = 3L;
	private static final String TAG_NAME = "family";
	
	private static final Long CERTIFICATE_ID_1 = 1L;
	
	@Autowired
	private TagRepository tagRepository;
	private TagModel tag1;
	private TagModel tag2;
	private TagModel tag3;

	@BeforeEach
	public void setUp() {
		tag1 = new TagModel();
		tag1.setId(TAG_ID_1);
		tag1.setName("food");
		tag1.setDeleted(false);
		
		tag2 = new TagModel();
		tag2.setId(TAG_ID_2);
		tag2.setName("family");
		tag2.setDeleted(false);

		tag3 = new TagModel();
		tag3.setId(TAG_ID_3);
		tag3.setName("new year");
		tag3.setDeleted(false);

	}

	@Test
	void testCreate() {
		TagModel actual = tagRepository.create(tag3);
		Assertions.assertEquals(tag3, actual);
	}

	@Test
	void testReadById() {
		TagModel actual = tagRepository.readById(TAG_ID_1);
		Assertions.assertEquals(tag1, actual);
	}

	@Test
	void testTagExistsById() {
		boolean actual = tagRepository.tagExistsById(TAG_ID_1);
		Assertions.assertTrue(actual);
	}

	@Test
	void testReadByCertificateId() {
		List<TagModel> actual = tagRepository.readByCertificateId(TAG_ID_1);
		List<TagModel> expected = Arrays.asList(tag1, tag2);
		Assertions.assertEquals(expected, actual);
	}

	@Test
	void testReadByName() {
		TagModel actual = tagRepository.readByName(TAG_NAME);
		Assertions.assertEquals(tag2, actual);
	}

	@Test
	void testTagExistsByName() {
		boolean actual = tagRepository.tagExistsByName(TAG_NAME);
		Assertions.assertTrue(actual);
	}

	@Test
	void testReadAll() {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
		tagRepository.create(tag3);
		params.add("page", "2");
		params.add("limit", "2");

		List<TagModel> actual = tagRepository.readAll(params);
		List<TagModel> expected = Arrays.asList(tag3);
		Assertions.assertEquals(expected, actual);
	}

	@Test
	void testDelete() {
		int actual = tagRepository.delete(TAG_ID_1);
		Assertions.assertEquals(1, actual);
	}

	@Test
	void testSaveTagsForCertificate() {
		tagRepository.create(tag3);
		List<TagModel> tags = Arrays.asList(tag3);
		int actual = tagRepository.saveTagsForCertificate(CERTIFICATE_ID_1, tags);
		Assertions.assertEquals(1, actual);
	}

	@Test
	void testRestore() {
		TagModel expected = new TagModel();
		expected.setId(TAG_ID_3);
		expected.setName(TAG_NAME);
		expected.setDeleted(true);
		TagModel actual = tagRepository.restore(expected);
		expected.setDeleted(false);
		Assertions.assertEquals(expected, actual);
	}

	@Test
	void testDeleteAllTagsForCertificate() {
		int actual = tagRepository.deleteAllTagsForCertificate(CERTIFICATE_ID_1);
		Assertions.assertEquals(2, actual);
	}

}
