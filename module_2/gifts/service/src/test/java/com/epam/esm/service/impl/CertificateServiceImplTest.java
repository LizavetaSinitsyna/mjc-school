package com.epam.esm.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
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
import com.epam.esm.dto.TagDto;
import com.epam.esm.exception.NullEntityException;
import com.epam.esm.exception.ValidationException;
import com.epam.esm.repository.CertificateRepository;
import com.epam.esm.repository.TagRepository;
import com.epam.esm.repository.model.CertificateModel;
import com.epam.esm.service.TagService;
import com.epam.esm.service.converter.CertificateConverter;
import com.epam.esm.service.converter.TagConverter;
import com.epam.esm.service.validation.CertificateValidation;
import com.epam.esm.service.validation.TagValidation;

@ExtendWith(MockitoExtension.class)
class CertificateServiceImplTest {
	private static final long ID = 1L;
	@Mock
	private CertificateRepository certificateRepository;

	@Mock
	private TagRepository tagRepository;

	@Mock
	private TagService tagService;

	@Mock
	private CertificateValidation certificateValidation;

	@Mock
	private TagValidation tagValidation;

	@Mock
	private CertificateConverter certificateConverter;

	@Mock
	private TagConverter tagConverter;

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
		Mockito.doThrow(ValidationException.class).when(certificateValidation).checkCertificateExistenceById(ID);

		Assertions.assertThrows(ValidationException.class, () -> {
			certificateServiceImpl.readById(ID);
		});
	}

	@Test
	void testReadAll() {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

		CertificateModel certificateModel = new CertificateModel();
		certificateModel.setId(ID);
		CertificateDto certificateDto = new CertificateDto();
		certificateDto.setId(ID);

		List<CertificateModel> certificateModels = new ArrayList<>();
		certificateModels.add(certificateModel);

		List<CertificateDto> expected = new ArrayList<>();
		expected.add(certificateDto);

		Mockito.when(certificateRepository.readAll(params)).thenReturn(certificateModels);
		Mockito.when(certificateConverter.convertToDto(certificateModel)).thenReturn(certificateDto);

		Assertions.assertEquals(expected, certificateServiceImpl.readAll(params));

		Mockito.verify(certificateValidation).validateReadParams(params);
		Mockito.verify(tagService).readByCertificateId(ID);
	}

	@Test
	void testReadAllWithNullParams() {
		Assertions.assertThrows(NullEntityException.class, () -> {
			certificateServiceImpl.readAll(null);
		});
	}

	@Test
	void testDelete() {
		certificateServiceImpl.delete(ID);
		Mockito.verify(certificateValidation).checkCertificateExistenceById(ID);
		Mockito.verify(certificateRepository).delete(ID);
	}

	@Test
	void testDeleteWithValidationException() {
		Mockito.doThrow(ValidationException.class).when(certificateValidation)
				.checkCertificateExistenceById(Mockito.anyLong());

		Assertions.assertThrows(ValidationException.class, () -> {
			certificateServiceImpl.delete(Mockito.anyLong());
		});
	}

	@Test
	void testUpdateCertificateFields() {
		CertificateModel certificateModel = Mockito.mock(CertificateModel.class);
		CertificateDto expected = Mockito.mock(CertificateDto.class);

		Mockito.when(certificateConverter.convertToModel(expected)).thenReturn(certificateModel);
		Mockito.when(certificateRepository.updateCertificateFields(certificateModel)).thenReturn(certificateModel);
		Mockito.when(certificateConverter.convertToDto(certificateModel)).thenReturn(expected);

		CertificateDto actual = certificateServiceImpl.updateCertificateFields(ID, expected);

		Assertions.assertEquals(expected, actual);

		Mockito.verify(certificateRepository).updateCertificateFields(certificateModel);
		Mockito.verify(certificateValidation).validateCertificateAllFieldsRequirementsForPatchUpdate(ID, actual);
		Mockito.verify(certificateConverter).convertToDto(certificateModel);
		Mockito.verify(certificateConverter).convertToModel(expected);
		Mockito.verify(tagRepository).deleteAllTagsForCertificate(Mockito.anyLong());
		Mockito.verify(tagRepository).saveTagsForCertificate(Mockito.anyLong(), Mockito.any());
	}

	@Test
	void testUpdateEntireCertificateWitnEmptyTags() {
		CertificateModel certificateModel = Mockito.mock(CertificateModel.class);
		CertificateDto expected = Mockito.mock(CertificateDto.class);

		Mockito.when(certificateConverter.convertToModel(expected)).thenReturn(certificateModel);
		Mockito.when(certificateRepository.updateEntireCertificate(certificateModel)).thenReturn(certificateModel);
		Mockito.when(certificateConverter.convertToDto(certificateModel)).thenReturn(expected);

		CertificateDto actual = certificateServiceImpl.updateEntireCertificate(ID, expected);

		Assertions.assertEquals(expected, actual);

		Mockito.verify(certificateRepository).updateEntireCertificate(certificateModel);
		Mockito.verify(certificateValidation).validateCertificateAllFieldsRequirementsForEntireUpdate(ID, actual);
		Mockito.verify(certificateConverter).convertToDto(certificateModel);
		Mockito.verify(certificateConverter).convertToModel(expected);
		Mockito.verify(tagRepository).deleteAllTagsForCertificate(Mockito.anyLong());
		Mockito.verify(tagRepository).saveTagsForCertificate(Mockito.anyLong(), Mockito.any());
	}

	@Test
	void testUpdateEntireCertificateWithTags() {
		CertificateModel certificateModel = Mockito.mock(CertificateModel.class);
		CertificateDto expected = new CertificateDto();
		List<TagDto> tagDtos = Arrays.asList(new TagDto(), new TagDto());
		expected.setTags(tagDtos);

		Mockito.when(certificateConverter.convertToModel(expected)).thenReturn(certificateModel);
		Mockito.when(certificateRepository.updateEntireCertificate(certificateModel)).thenReturn(certificateModel);
		Mockito.when(certificateConverter.convertToDto(certificateModel)).thenReturn(expected);

		CertificateDto actual = certificateServiceImpl.updateEntireCertificate(ID, expected);

		Assertions.assertEquals(expected, actual);

		Mockito.verify(certificateRepository).updateEntireCertificate(certificateModel);
		Mockito.verify(certificateValidation).validateCertificateAllFieldsRequirementsForEntireUpdate(ID, actual);
		Mockito.verify(certificateConverter).convertToDto(certificateModel);
		Mockito.verify(certificateConverter).convertToModel(expected);
		Mockito.verify(tagRepository).deleteAllTagsForCertificate(Mockito.anyLong());
		Mockito.verify(tagRepository).saveTagsForCertificate(Mockito.anyLong(), Mockito.any());
		Mockito.verify(tagValidation).validateAllTagFields(Mockito.any());
	}

}
