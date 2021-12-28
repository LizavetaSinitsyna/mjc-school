package com.epam.esm.repository.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.epam.esm.repository.CertificateRepository;
import com.epam.esm.repository.config.JdbcTestConfiguration;
import com.epam.esm.repository.model.CertificateModel;

@SpringJUnitConfig(JdbcTestConfiguration.class)
@SqlGroup({ @Sql(scripts = "/dropTables.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD),
		@Sql(scripts = "/schema.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD),
		@Sql(scripts = "/data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD) })
class CertificateRepositoryImplTest {
	private static final Long CERTIFICATE_ID_1 = 1L;
	private static final Long CERTIFICATE_ID_2 = 2L;
	private static final Long CERTIFICATE_ID_3 = 3L;
	private static final Long TAG_ID_2 = 2L;
	private static final int OFFSET = 0;
	private static final int LIMIT = 10;

	private static final String CERTIFICATE_NAME = "Certificate for Museum of Arts";

	private CertificateModel certificate1;
	private CertificateModel certificate2;
	private CertificateModel certificate3;

	@Autowired
	private CertificateRepository certificateRepository;

	@BeforeEach
	public void setUp() {
		certificate1 = new CertificateModel();
		certificate1.setId(CERTIFICATE_ID_1);
		certificate1.setName("Dinner at the restaurant with unlimited pizzas");
		certificate1.setDescription("Great present for those who loves pizza");
		certificate1.setPrice(new BigDecimal("50.00"));
		certificate1.setCreateDate(LocalDateTime.parse("2021-12-14T00:39:00"));
		certificate1.setLastUpdateDate(LocalDateTime.parse("2021-12-14T00:39:00"));
		certificate1.setDuration(30);

		certificate2 = new CertificateModel();
		certificate2.setId(CERTIFICATE_ID_2);
		certificate2.setName("Certificate for Museum of Arts");
		certificate2.setDescription("Interesting journey into history of art");
		certificate2.setPrice(new BigDecimal("45.00"));
		certificate2.setCreateDate(LocalDateTime.parse("2021-12-10T12:45:11"));
		certificate2.setLastUpdateDate(LocalDateTime.parse("2021-12-10T12:45:11"));
		certificate2.setDuration(90);

		certificate3 = new CertificateModel();
		certificate3.setId(CERTIFICATE_ID_3);
		certificate3.setName("Certificate to the Zoo");
		certificate3.setDescription("Wonderful trip to the Dipriz zoo in Baranovichi");
		certificate3.setPrice(new BigDecimal("100.00"));
		certificate3.setCreateDate(LocalDateTime.parse("2021-12-14T11:45:11"));
		certificate3.setLastUpdateDate(LocalDateTime.parse("2021-12-14T11:45:11"));
		certificate3.setDuration(365);
	}

	@Test
	void testCreate() {
		CertificateModel actual = certificateRepository.save(certificate3);
		Assertions.assertEquals(certificate3, actual);
	}

	@Test
	void testReadById() {
		Optional<CertificateModel> actual = certificateRepository.findById(CERTIFICATE_ID_1);
		Assertions.assertEquals(Optional.of(certificate1), actual);
	}

	@Test
	void testReadByName() {
		Optional<CertificateModel> actual = certificateRepository.findByName(CERTIFICATE_NAME);
		Assertions.assertEquals(Optional.of(certificate2), actual);
	}

	@Test
	void testCertificateExistsByName() {
		boolean actual = certificateRepository.certificateExistsByName(CERTIFICATE_NAME);
		Assertions.assertTrue(actual);
	}

	@Test
	void testReadAll() {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
		certificateRepository.save(certificate3);
		params.add("page", "1");
		params.add("limit", "1");
		params.add("sort", "price-");

		List<CertificateModel> actual = certificateRepository.findAll(params, OFFSET, LIMIT);
		List<CertificateModel> expected = Arrays.asList(certificate3);
		Assertions.assertEquals(expected, actual);
	}

	@Test
	void testUpdateCertificate() {
		CertificateModel expected = new CertificateModel();
		expected.setId(CERTIFICATE_ID_3);
		expected.setName("Certificate to the Zoo");
		expected.setDescription("Wonderful trip to the Dipriz zoo in Baranovichi");
		expected.setPrice(new BigDecimal("100.00"));
		expected.setCreateDate(LocalDateTime.parse("2021-12-14T11:45:11"));
		expected.setLastUpdateDate(LocalDateTime.parse("2021-12-14T11:45:11"));
		expected.setDuration(365);
		certificateRepository.save(expected);
		expected.setDuration(50);
		
		CertificateModel actual = certificateRepository.updateCertificate(expected);

		Assertions.assertEquals(expected, actual);
	}

	@Test
	void testReadByTagId() {
		List<CertificateModel> actual = certificateRepository.readByTagId(TAG_ID_2);
		List<CertificateModel> expected = Arrays.asList(certificate1, certificate2);
		Assertions.assertEquals(expected, actual);
	}

	@Test
	void testDelete() {
		int actual = certificateRepository.delete(CERTIFICATE_ID_1);
		Assertions.assertEquals(1, actual);
	}

}
