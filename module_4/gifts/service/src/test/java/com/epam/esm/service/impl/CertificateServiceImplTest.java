package com.epam.esm.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.epam.esm.dto.CertificateDto;
import com.epam.esm.dto.TagDto;
import com.epam.esm.exception.NullEntityException;
import com.epam.esm.exception.ValidationException;
import com.epam.esm.repository.CertificateRepository;
import com.epam.esm.repository.TagRepository;
import com.epam.esm.repository.model.CertificateModel;
import com.epam.esm.repository.model.EntityConstant;
import com.epam.esm.repository.model.TagModel;
import com.epam.esm.service.CertificateService;
import com.epam.esm.service.ServiceConstant;
import com.epam.esm.service.converter.CertificateConverter;
import com.epam.esm.service.converter.PageConverter;
import com.epam.esm.service.converter.TagConverter;
import com.epam.esm.service.validation.CertificateValidation;
import com.epam.esm.service.validation.TagValidation;

class CertificateServiceImplTest {
	private static final Long CERTIFICATE_ID_1 = 1L;
	private static final Long TAG_ID_1 = 1L;
	private static final Long INVALID_ID = -1L;

	private CertificateModel certificateModel1;
	private CertificateDto certificateDto1;
	private TagModel tagModel1;
	private TagDto tagDto1;
	private Page<CertificateModel> certificateModelsPage;
	private Page<CertificateDto> certificateDtosPage;

	private CertificateRepository certificateRepository;
	private TagRepository tagRepository;

	private static CertificateValidation certificateValidation;
	private static TagValidation tagValidation;
	private static CertificateConverter certificateConverter;
	private static TagConverter tagConverter;
	private static PageConverter<CertificateDto, CertificateModel> pageConverter;
	private static LocalDateTime localDateTime;

	private CertificateService certificateServiceImpl;

	@BeforeAll
	public static void init() {
		certificateValidation = new CertificateValidation();
		tagValidation = new TagValidation();
		certificateConverter = new CertificateConverter();
		tagConverter = new TagConverter();
		pageConverter = new PageConverter<>();
		localDateTime = LocalDateTime.now();
	}

	@BeforeEach
	public void setUp() {
		certificateRepository = Mockito.mock(CertificateRepository.class);
		tagRepository = Mockito.mock(TagRepository.class);

		certificateServiceImpl = new CertificateServiceImpl(certificateRepository, tagRepository, certificateValidation,
				tagValidation, certificateConverter, tagConverter, pageConverter);

		certificateModel1 = new CertificateModel();
		certificateModel1.setName("Dinner at the restaurant with unlimited pizzas");
		certificateModel1.setDescription("Great present for those who loves pizza");
		certificateModel1.setPrice(new BigDecimal("50.00"));
		certificateModel1.setCreateDate(localDateTime);
		certificateModel1.setLastUpdateDate(localDateTime);
		certificateModel1.setDuration(30);

		certificateDto1 = new CertificateDto();
		certificateDto1.setName("Dinner at the restaurant with unlimited pizzas");
		certificateDto1.setDescription("Great present for those who loves pizza");
		certificateDto1.setPrice(new BigDecimal("50.00"));
		certificateDto1.setCreateDate(localDateTime);
		certificateDto1.setLastUpdateDate(localDateTime);
		certificateDto1.setDuration(30);

		tagModel1 = new TagModel();
		tagModel1.setId(TAG_ID_1);
		tagModel1.setName("food");
		tagModel1.setDeleted(false);

		tagDto1 = new TagDto();
		tagDto1.setId(TAG_ID_1);
		tagDto1.setName("food");

		certificateDto1.setTags(Arrays.asList(tagDto1));
		certificateModel1.setTags(Arrays.asList(tagModel1));

		List<CertificateModel> certificateModels = new ArrayList<>();
		certificateModels.add(certificateModel1);
		List<CertificateDto> certificateDtos = new ArrayList<>();
		certificateDtos.add(certificateDto1);

		Pageable pageable = PageRequest.of(ServiceConstant.DEFAULT_PAGE_NUMBER, ServiceConstant.DEFAULT_LIMIT);
		certificateModelsPage = new PageImpl<>(certificateModels, pageable, certificateModels.size());
		certificateDtosPage = new PageImpl<>(certificateDtos, pageable, certificateModels.size());
	}

	@Test
	void testCreate() {
		CertificateDto expected = certificateDto1;

		Mockito.when(certificateRepository.save(Mockito.any())).thenReturn(certificateModel1);
		Mockito.when(certificateRepository.certificateExistsByName(Mockito.any())).thenReturn(false);
		Mockito.when(tagRepository.save(Mockito.any())).thenReturn(tagModel1);
		Mockito.when(tagRepository.findByName(Mockito.any())).thenReturn(Optional.ofNullable(null));

		CertificateDto actual = certificateServiceImpl.create(expected);

		Assertions.assertEquals(expected, actual);

		Mockito.verify(certificateRepository).save(Mockito.any());
		Mockito.verify(certificateRepository).certificateExistsByName(Mockito.any());
	}

	@Test
	void testCreateWithInvalidPrice() {
		certificateDto1.setPrice(new BigDecimal("-5.00"));
		Assertions.assertThrows(ValidationException.class, () -> {
			certificateServiceImpl.create(certificateDto1);
		});
	}

	@Test
	void testCreateCertificates() {
		List<CertificateDto> expected = new ArrayList<>();
		expected.add(certificateDto1);

		Mockito.when(certificateRepository.saveCertificates(Mockito.any()))
				.thenReturn(Arrays.asList(certificateModel1));
		Mockito.when(certificateRepository.certificateExistsByName(Mockito.any())).thenReturn(false);
		Mockito.when(tagRepository.save(Mockito.any())).thenReturn(tagModel1);
		Mockito.when(tagRepository.findByName(Mockito.any())).thenReturn(Optional.ofNullable(null));

		List<CertificateDto> actual = certificateServiceImpl.createCertificates(expected);

		Assertions.assertEquals(expected, actual);

		Mockito.verify(certificateRepository).saveCertificates(Mockito.any());
		Mockito.verify(certificateRepository).certificateExistsByName(Mockito.any());
	}

	@Test
	void testReadById() {
		CertificateDto expected = certificateDto1;

		Mockito.when(certificateRepository.findById(CERTIFICATE_ID_1)).thenReturn(Optional.of(certificateModel1));
		CertificateDto actual = certificateServiceImpl.readById(CERTIFICATE_ID_1);

		Assertions.assertEquals(expected, actual);

		Mockito.verify(certificateRepository).findById(CERTIFICATE_ID_1);
	}

	@Test
	void testReadByIdWithInvalidId() {
		Assertions.assertThrows(ValidationException.class, () -> {
			certificateServiceImpl.readById(INVALID_ID);
		});
	}

	@Test
	void testReadAll() {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

		Page<CertificateDto> expected = certificateDtosPage;

		Mockito.when(certificateRepository.findAll(params, ServiceConstant.DEFAULT_PAGE_NUMBER,
				ServiceConstant.DEFAULT_LIMIT)).thenReturn(certificateModelsPage);
		Page<CertificateDto> actual = certificateServiceImpl.readAll(params);
		Assertions.assertEquals(expected, actual);

		Mockito.verify(certificateRepository).findAll(params, ServiceConstant.DEFAULT_PAGE_NUMBER,
				ServiceConstant.DEFAULT_LIMIT);
	}

	@Test
	void testReadAllWithOrderBy() {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.put(EntityConstant.ORDER_BY, Arrays.asList(EntityConstant.CERTIFICATE_CREATE_DATE));

		Page<CertificateDto> expected = certificateDtosPage;

		Mockito.when(certificateRepository.findAll(Mockito.any(), Mockito.eq(ServiceConstant.DEFAULT_PAGE_NUMBER),
				Mockito.eq(ServiceConstant.DEFAULT_LIMIT))).thenReturn(certificateModelsPage);
		Page<CertificateDto> actual = certificateServiceImpl.readAll(params);
		Assertions.assertEquals(expected, actual);

		Mockito.verify(certificateRepository).findAll(Mockito.any(), Mockito.eq(ServiceConstant.DEFAULT_PAGE_NUMBER),
				Mockito.eq(ServiceConstant.DEFAULT_LIMIT));
	}

	@Test
	void testReadAllWithNullParams() {
		Assertions.assertThrows(NullEntityException.class, () -> {
			certificateServiceImpl.readAll(null);
		});
	}

	@Test
	void testDelete() {
		Mockito.when(certificateRepository.delete(CERTIFICATE_ID_1)).thenReturn(1);
		Mockito.when(certificateRepository.certificateExistsById(CERTIFICATE_ID_1)).thenReturn(true);
		certificateServiceImpl.delete(CERTIFICATE_ID_1);

		Mockito.verify(certificateRepository).delete(CERTIFICATE_ID_1);
	}

	@Test
	void testDeleteWithInvalidId() {
		Assertions.assertThrows(ValidationException.class, () -> {
			certificateServiceImpl.delete(INVALID_ID);
		});
	}

	@Test
	void testUpdateCertificateFields() {
		CertificateDto expected = certificateDto1;
		certificateModel1.setId(CERTIFICATE_ID_1);
		Mockito.when(certificateRepository.updateCertificate(Mockito.any())).thenReturn(certificateModel1);
		Mockito.when(certificateRepository.findByName(Mockito.any())).thenReturn(Optional.of(certificateModel1));
		Mockito.when(certificateRepository.findById(CERTIFICATE_ID_1)).thenReturn(Optional.of(certificateModel1));
		Mockito.when(certificateRepository.certificateExistsById(CERTIFICATE_ID_1)).thenReturn(true);
		Mockito.when(tagRepository.save(Mockito.any())).thenReturn(tagModel1);
		Mockito.when(tagRepository.findByName(Mockito.any())).thenReturn(Optional.ofNullable(null));

		CertificateDto actual = certificateServiceImpl.updateCertificateFields(CERTIFICATE_ID_1, expected);

		Assertions.assertEquals(expected, actual);

		Mockito.verify(certificateRepository).updateCertificate(certificateModel1);
		Mockito.verify(certificateRepository).findByName(Mockito.any());
		Mockito.verify(certificateRepository).certificateExistsById(CERTIFICATE_ID_1);
		Mockito.verify(tagRepository).findByName(Mockito.any());
	}

	@Test
	void testUpdateEntireCertificate() {
		CertificateDto expected = certificateDto1;
		certificateModel1.setId(CERTIFICATE_ID_1);

		Mockito.when(certificateRepository.updateCertificate(Mockito.any())).thenReturn(certificateModel1);
		Mockito.when(certificateRepository.findByName(Mockito.any())).thenReturn(Optional.of(certificateModel1));
		Mockito.when(certificateRepository.findById(CERTIFICATE_ID_1)).thenReturn(Optional.of(certificateModel1));
		Mockito.when(certificateRepository.certificateExistsById(CERTIFICATE_ID_1)).thenReturn(true);
		Mockito.when(tagRepository.save(Mockito.any())).thenReturn(tagModel1);
		Mockito.when(tagRepository.findByName(Mockito.any())).thenReturn(Optional.ofNullable(null));

		CertificateDto actual = certificateServiceImpl.updateEntireCertificate(CERTIFICATE_ID_1, expected);

		Assertions.assertEquals(expected, actual);

		Mockito.verify(certificateRepository).updateCertificate(certificateModel1);
		Mockito.verify(certificateRepository).findByName(Mockito.any());
		Mockito.verify(certificateRepository).certificateExistsById(CERTIFICATE_ID_1);
		Mockito.verify(tagRepository).findByName(Mockito.any());
	}
}
