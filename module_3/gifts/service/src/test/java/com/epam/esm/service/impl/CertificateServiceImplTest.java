package com.epam.esm.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.epam.esm.dto.CertificateDto;
import com.epam.esm.dto.TagDto;
import com.epam.esm.exception.NotFoundException;
import com.epam.esm.exception.NullEntityException;
import com.epam.esm.exception.ValidationException;
import com.epam.esm.repository.CertificateRepository;
import com.epam.esm.repository.TagRepository;
import com.epam.esm.repository.model.CertificateModel;
import com.epam.esm.repository.model.TagModel;
import com.epam.esm.service.CertificateService;
import com.epam.esm.service.converter.CertificateConverter;
import com.epam.esm.service.converter.TagConverter;
import com.epam.esm.service.validation.CertificateValidation;
import com.epam.esm.service.validation.TagValidation;

@ExtendWith(MockitoExtension.class)
class CertificateServiceImplTest {
	private static final Long CERTIFICATE_ID_1 = 1L;
	private static final Long TAG_ID_1 = 1L;
	private static final Long INVALID_ID = -1L;
	private static final int OFFSET = 0;
	private static final int LIMIT = 10;
	private CertificateModel certificateModel1;
	private CertificateDto certificateDto1;
	private TagModel tagModel1;
	private TagDto tagDto1;

	private CertificateRepository certificateRepository;
	private TagRepository tagRepository;

	private static CertificateValidation certificateValidation;
	private static TagValidation tagValidation;
	private static CertificateConverter certificateConverter;
	private static TagConverter tagConverter;
	private static LocalDateTime localDateTime;

	private CertificateService certificateServiceImpl;

	@BeforeAll
	public static void init() {
		certificateValidation = new CertificateValidation();
		tagValidation = new TagValidation();
		certificateConverter = new CertificateConverter();
		tagConverter = new TagConverter();
		localDateTime = LocalDateTime.now();
	}

	@BeforeEach
	public void setUp() {
		certificateRepository = Mockito.mock(CertificateRepository.class);
		tagRepository = Mockito.mock(TagRepository.class);

		certificateServiceImpl = new CertificateServiceImpl(certificateRepository, tagRepository, certificateValidation,
				tagValidation, certificateConverter, tagConverter);

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

		List<CertificateModel> certificateModels = Arrays.asList(certificateModel1);
		List<CertificateDto> expected = Arrays.asList(certificateDto1);

		Mockito.when(certificateRepository.findAll(params, OFFSET, LIMIT)).thenReturn(certificateModels);
		List<CertificateDto> actual = certificateServiceImpl.readAll(params);
		Assertions.assertEquals(expected, actual);

		Mockito.verify(certificateRepository).findAll(params, OFFSET, LIMIT);
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
		Mockito.when(certificateRepository.findById(CERTIFICATE_ID_1)).thenReturn(Optional.of(certificateModel1));
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
		Mockito.when(tagRepository.save(Mockito.any())).thenReturn(tagModel1);
		Mockito.when(tagRepository.findByName(Mockito.any())).thenReturn(Optional.ofNullable(null));
		
		CertificateDto actual = certificateServiceImpl.updateCertificateFields(CERTIFICATE_ID_1, expected);

		Assertions.assertEquals(expected, actual);

		Mockito.verify(certificateRepository).updateCertificate(certificateModel1);
		Mockito.verify(certificateRepository).findByName(Mockito.any());
		Mockito.verify(certificateRepository, Mockito.atLeast(1)).findById(CERTIFICATE_ID_1);
		Mockito.verify(tagRepository).findByName(Mockito.any());
	}

	@Test
	void testUpdateEntireCertificate() {
		CertificateDto expected = certificateDto1;
		certificateModel1.setId(CERTIFICATE_ID_1);
		Mockito.when(certificateRepository.updateCertificate(Mockito.any())).thenReturn(certificateModel1);
		Mockito.when(certificateRepository.findByName(Mockito.any())).thenReturn(Optional.of(certificateModel1));
		Mockito.when(certificateRepository.findById(CERTIFICATE_ID_1)).thenReturn(Optional.of(certificateModel1));
		Mockito.when(tagRepository.save(Mockito.any())).thenReturn(tagModel1);
		Mockito.when(tagRepository.findByName(Mockito.any())).thenReturn(Optional.ofNullable(null));

		CertificateDto actual = certificateServiceImpl.updateEntireCertificate(CERTIFICATE_ID_1, expected);

		Assertions.assertEquals(expected, actual);

		Mockito.verify(certificateRepository).updateCertificate(certificateModel1);
		Mockito.verify(certificateRepository).findByName(Mockito.any());
		Mockito.verify(certificateRepository, Mockito.atLeast(1)).findById(CERTIFICATE_ID_1);
		Mockito.verify(tagRepository).findByName(Mockito.any());
	}

	@Test
	void testCheckCertificateExistenceByIdWithNoExistedCertificate() {
		Mockito.when(certificateRepository.findById(CERTIFICATE_ID_1)).thenReturn(Optional.ofNullable(null));
		Assertions.assertThrows(NotFoundException.class, () -> {
			certificateServiceImpl.checkCertificateExistenceById(CERTIFICATE_ID_1);
		});
	}

	@Test
	void testCheckCertificateExistenceByIdWithInvalidId() {
		Assertions.assertThrows(ValidationException.class, () -> {
			certificateServiceImpl.checkCertificateExistenceById(INVALID_ID);
		});
	}

}
