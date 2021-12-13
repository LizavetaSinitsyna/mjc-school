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
	}

	@Test
	void testReadById() {
		CertificateModel certificateModel = Mockito.mock(CertificateModel.class);
		CertificateDto expected = Mockito.mock(CertificateDto.class);

		Mockito.when(certificateRepository.readById(Mockito.anyLong())).thenReturn(certificateModel);
		Mockito.when(certificateConverter.convertToDto(certificateModel)).thenReturn(expected);

		Assertions.assertEquals(expected, certificateServiceImpl.readById(Mockito.anyLong()));

		Mockito.verify(certificateValidation).validateId(Mockito.anyLong());
		Mockito.verify(tagService).readByCertificateId(Mockito.anyLong());
	}

	@Test
	void testReadAll() {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		long id = 1;
		CertificateModel certificateModel = Mockito.mock(CertificateModel.class);
		certificateModel.setId(id);
		CertificateDto certificateDto = Mockito.mock(CertificateDto.class);
		certificateDto.setId(id);
		List<CertificateModel> certificateModels = new ArrayList<>();
		certificateModels.add(certificateModel);
		
		List<CertificateDto> expected = new ArrayList<>();
		expected.add(certificateDto);
		
		Mockito.when(certificateRepository.readAll(Mockito.any())).thenReturn(certificateModels);
		Mockito.when(certificateConverter.convertToDto(certificateModel)).thenReturn(certificateDto);
		
		Assertions.assertEquals(expected, certificateServiceImpl.readAll(params));
		
		Mockito.verify(certificateValidation).validateReadParams(Mockito.any());
		Mockito.verify(tagService).readByCertificateId(id);
	}

	@Test
	void testDelete() {
		fail("Not yet implemented");
	}

	@Test
	void testUpdateCertificateFields() {
		fail("Not yet implemented");
	}

	@Test
	void testUpdateEntireCertificate() {
		fail("Not yet implemented");
	}

}
