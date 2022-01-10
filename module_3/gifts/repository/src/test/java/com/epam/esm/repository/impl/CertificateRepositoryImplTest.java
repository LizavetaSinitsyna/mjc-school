package com.epam.esm.repository.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.epam.esm.repository.CertificateRepository;
import com.epam.esm.repository.model.CertificateModel;
import com.epam.esm.repository.model.EntityConstant;
import com.epam.esm.repository.model.TagModel;

@DataJpaTest
@EntityScan("com.epam.esm")
@ComponentScan("com.epam.esm")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class CertificateRepositoryImplTest {
	private static final Long CERTIFICATE_ID_1 = 1L;
	private static final int OFFSET = 0;
	private static final int LIMIT_2 = 2;
	private static final int DELETED_CERTIFICATES_AMOUNT = 1;

	private static final String CERTIFICATE_NAME = "certificate for Museum of Arts";
	private static final String SEARCH_PATTERN = "museum";
	private static final String TAG_NAME = "fAmILy";
	private static final long TAG_ID_1 = 1;

	private CertificateModel certificate1;
	private CertificateModel certificate2;
	private CertificateModel updatedCertificate1;
	private TagModel tag1;
	private TagModel tag2;

	@Autowired
	private CertificateRepository certificateRepository;

	@PersistenceContext
	private EntityManager entityManager;

	@BeforeEach
	public void setUp() {
		tag1 = new TagModel();
		tag1.setName("food");
		entityManager.persist(tag1);

		tag2 = new TagModel();
		tag2.setName("family");
		entityManager.persist(tag2);

		List<TagModel> tagList1 = new ArrayList<>();
		tagList1.add(tag1);
		tagList1.add(tag2);

		List<TagModel> tagList2 = new ArrayList<>();
		tagList2.add(tag2);

		List<TagModel> updatedTagList2 = new ArrayList<>();
		updatedTagList2.add(tag1);

		certificate1 = new CertificateModel();
		certificate1.setName("Dinner at the restaurant with unlimited pizzas");
		certificate1.setDescription("Great present for those who loves pizza");
		certificate1.setPrice(new BigDecimal("50.00"));
		certificate1.setDuration(30);
		certificate1.setTags(tagList1);

		certificate2 = new CertificateModel();
		certificate2.setName("Certificate for Museum of Arts");
		certificate2.setDescription("Interesting journey into history of art");
		certificate2.setPrice(new BigDecimal("45.00"));
		certificate2.setDuration(90);
		certificate2.setTags(tagList2);

		updatedCertificate1 = new CertificateModel();
		updatedCertificate1.setId(CERTIFICATE_ID_1);
		updatedCertificate1.setName("Dinner at the restaurant with 5 pizzas");
		updatedCertificate1.setDescription("Great present for those who loves pizza");
		updatedCertificate1.setPrice(new BigDecimal("50.00"));
		updatedCertificate1.setDuration(30);
		updatedCertificate1.setTags(updatedTagList2);
	}

	@Test
	void testSave() {
		CertificateModel actual = certificateRepository.save(certificate1);
		Assertions.assertEquals(certificate1, actual);
	}

	@Test
	void testSaveCertificates() {
		List<CertificateModel> expected = new ArrayList<>();
		expected.add(certificate1);
		expected.add(certificate2);
		List<CertificateModel> actual = certificateRepository.saveCertificates(expected);
		Assertions.assertEquals(expected, actual);
	}

	@Test
	void testFindById() {
		entityManager.persist(certificate1);
		Optional<CertificateModel> actual = certificateRepository.findById(CERTIFICATE_ID_1);
		Assertions.assertEquals(Optional.of(certificate1), actual);
	}

	@Test
	void testFindByName() {
		entityManager.persist(certificate2);
		Optional<CertificateModel> actual = certificateRepository.findByName(CERTIFICATE_NAME);
		Assertions.assertEquals(Optional.of(certificate2), actual);
	}

	@Test
	void testCertificateExistsByName() {
		entityManager.persist(certificate2);
		boolean actual = certificateRepository.certificateExistsByName(CERTIFICATE_NAME);
		Assertions.assertTrue(actual);
	}

	@Test
	void testReadAllWithSearch() {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
		entityManager.persist(certificate1);
		entityManager.persist(certificate2);
		params.add(EntityConstant.SEARCH, SEARCH_PATTERN);
		List<CertificateModel> actual = certificateRepository.findAll(params, OFFSET, LIMIT_2);
		List<CertificateModel> expected = Arrays.asList(certificate2);
		Assertions.assertEquals(expected, actual);
	}

	@Test
	void testReadAllWithTag() {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
		entityManager.persist(certificate1);
		entityManager.persist(certificate2);
		params.add(EntityConstant.TAG, TAG_NAME);
		List<CertificateModel> actual = certificateRepository.findAll(params, OFFSET, LIMIT_2);
		List<CertificateModel> expected = Arrays.asList(certificate1, certificate2);
		Assertions.assertEquals(expected, actual);
	}

	@Test
	void testFindAll() {
		entityManager.persist(certificate1);
		entityManager.persist(certificate2);
		List<CertificateModel> actual = certificateRepository.findAll(OFFSET, LIMIT_2);
		List<CertificateModel> expected = Arrays.asList(certificate1, certificate2);
		Assertions.assertEquals(expected, actual);
	}

	@Test
	void testUpdateCertificate() {
		entityManager.persist(certificate1);
		CertificateModel actual = certificateRepository.updateCertificate(updatedCertificate1);
		Assertions.assertEquals(certificate1, actual);
	}

	@Test
	void testReadByTagId() {
		entityManager.persist(certificate1);
		List<CertificateModel> actual = certificateRepository.readByTagId(TAG_ID_1);
		List<CertificateModel> expected = Arrays.asList(certificate1);
		Assertions.assertEquals(expected, actual);
	}

	@Test
	void testDelete() {
		entityManager.persist(certificate1);
		int actual = certificateRepository.delete(CERTIFICATE_ID_1);
		Assertions.assertEquals(DELETED_CERTIFICATES_AMOUNT, actual);
	}
}
