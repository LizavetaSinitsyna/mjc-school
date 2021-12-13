package com.epam.esm.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.epam.esm.dto.CertificateDto;
import com.epam.esm.exception.NullEntityException;
import com.epam.esm.exception.ValidationException;
import com.epam.esm.repository.CertificateRepository;
import com.epam.esm.repository.TagRepository;
import com.epam.esm.repository.model.CertificateModel;
import com.epam.esm.service.TagService;
import com.epam.esm.service.converter.CertificateConverter;
import com.epam.esm.service.validation.CertificateValidation;

@ExtendWith(MockitoExtension.class)
class CertificateServiceImplTest {
	@Mock
	private CertificateRepository certificateRepository;

	@Mock
	private TagRepository tagRepository;

	@Mock
	private TagService tagService;

	@Mock
	private CertificateValidation certificateValidation;

	@Mock
	private CertificateConverter certificateConverter;

	@InjectMocks
	private CertificateServiceImpl certificateServiceImpl;

	@Test
	void testCreate() {
		CertificateModel certificateModel = Mockito.mock(CertificateModel.class);
		CertificateDto expected = Mockito.mock(CertificateDto.class);

		Mockito.when(certificateConverter.convertToModel(expected)).thenReturn(certificateModel);
		Mockito.when(certificateRepository.create(certificateModel)).thenReturn(certificateModel);
		Mockito.when(certificateConverter.convertToDto(certificateModel)).thenReturn(expected);

		CertificateDto actual = certificateServiceImpl.create(expected);

		Assertions.assertEquals(expected, actual);

		Mockito.verify(certificateRepository).create(certificateModel);
		Mockito.verify(certificateValidation).validateCertificateAllFieldsRequirementsForCreate(expected);
		Mockito.verify(certificateConverter).convertToDto(certificateModel);
		Mockito.verify(certificateConverter).convertToModel(expected);
		Mockito.verify(tagRepository).deleteAllTagsForCertificate(Mockito.anyLong());
		Mockito.verify(tagRepository).saveTagsForCertificate(Mockito.anyLong(), Mockito.any());
	}

	@Test
	void testCreateWithValidationException() {
		Mockito.doThrow(ValidationException.class).when(certificateValidation)
				.validateCertificateAllFieldsRequirementsForCreate(Mockito.any());

		Assertions.assertThrows(ValidationException.class, () -> {
			certificateServiceImpl.create(Mockito.any());
		});
	}

	@Test
	void testReadById() {
		CertificateModel certificateModel = new CertificateModel();
		CertificateDto expected = new CertificateDto();

		Mockito.when(certificateRepository.readById(Mockito.anyLong())).thenReturn(certificateModel);
		Mockito.when(certificateConverter.convertToDto(certificateModel)).thenReturn(expected);

		Assertions.assertEquals(expected, certificateServiceImpl.readById(Mockito.anyLong()));

		Mockito.verify(certificateValidation).checkCertificateExistenceById(Mockito.anyLong());
		Mockito.verify(tagService).readByCertificateId(Mockito.anyLong());
		Mockito.verify(certificateRepository).readById(Mockito.anyLong());
		Mockito.verify(certificateConverter).convertToDto(certificateModel);
	}

	@Test
	void testReadByIdWithValidationException() {
		Mockito.doThrow(ValidationException.class).when(certificateValidation).checkCertificateExistenceById(Mockito.anyLong());

		Assertions.assertThrows(ValidationException.class, () -> {
			certificateServiceImpl.readById(Mockito.anyLong());
		});
	}

	@Test
	void testReadAll() {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		long id = 1;
		CertificateModel certificateModel = new CertificateModel();
		certificateModel.setId(id);
		CertificateDto certificateDto = new CertificateDto();
		certificateDto.setId(id);

		List<CertificateModel> certificateModels = new ArrayList<>();
		certificateModels.add(certificateModel);

		List<CertificateDto> expected = new ArrayList<>();
		expected.add(certificateDto);

		Mockito.when(certificateRepository.readAll(params)).thenReturn(certificateModels);
		Mockito.when(certificateConverter.convertToDto(certificateModel)).thenReturn(certificateDto);

		Assertions.assertEquals(expected, certificateServiceImpl.readAll(params));

		Mockito.verify(certificateValidation).validateReadParams(params);
		Mockito.verify(tagService).readByCertificateId(id);
	}

	@Test
	void testReadAllWithNullParams() {
		Assertions.assertThrows(NullEntityException.class, () -> {
			certificateServiceImpl.readAll(null);
		});
	}

	@Test
	void testDelete() {
		certificateServiceImpl.delete(Mockito.anyLong());
		Mockito.verify(certificateValidation).checkCertificateExistenceById(Mockito.anyLong());
		Mockito.verify(certificateRepository).delete(Mockito.anyLong());
	}

	@Test
	void testDeleteWithValidationException() {
		Mockito.doThrow(ValidationException.class).when(certificateValidation).checkCertificateExistenceById(Mockito.anyLong());

		Assertions.assertThrows(ValidationException.class, () -> {
			certificateServiceImpl.delete(Mockito.anyLong());
		});
	}

	@Test
	void testUpdateCertificateFields() {
		CertificateModel certificateModel = Mockito.mock(CertificateModel.class);
		CertificateDto expected = Mockito.mock(CertificateDto.class);

		Mockito.when(certificateConverter.convertToModel(expected)).thenReturn(certificateModel);
		Mockito.when(certificateRepository.create(certificateModel)).thenReturn(certificateModel);
		Mockito.when(certificateConverter.convertToDto(certificateModel)).thenReturn(expected);

		CertificateDto actual = certificateServiceImpl.create(expected);

		Assertions.assertEquals(expected, actual);

		Mockito.verify(certificateRepository).create(certificateModel);
		Mockito.verify(certificateValidation).validateCertificateAllFieldsRequirementsForCreate(expected);
		Mockito.verify(certificateConverter).convertToDto(certificateModel);
		Mockito.verify(certificateConverter).convertToModel(expected);
		Mockito.verify(tagRepository).deleteAllTagsForCertificate(Mockito.anyLong());
		Mockito.verify(tagRepository).saveTagsForCertificate(Mockito.anyLong(), Mockito.any());

		Mockito.when(certificateRepository.readById(Mockito.anyLong())).thenReturn(certificateModel);
		Mockito.when(certificateConverter.convertToDto(certificateModel)).thenReturn(expected);

		Assertions.assertEquals(expected, certificateServiceImpl.readById(Mockito.anyLong()));

		Mockito.verify(certificateValidation).validateId(Mockito.anyLong());
		Mockito.verify(tagService).readByCertificateId(Mockito.anyLong());
		Mockito.verify(certificateRepository).readById(Mockito.anyLong());
		Mockito.verify(certificateConverter).convertToDto(certificateModel);
	}

	@Test
	void testUpdateEntireCertificate() {
		fail("Not yet implemented");
	}

}
