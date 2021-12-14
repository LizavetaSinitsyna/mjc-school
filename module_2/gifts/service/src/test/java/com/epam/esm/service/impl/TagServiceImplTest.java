package com.epam.esm.service.impl;

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

import com.epam.esm.dto.TagDto;
import com.epam.esm.repository.CertificateRepository;
import com.epam.esm.repository.TagRepository;
import com.epam.esm.repository.model.CertificateModel;
import com.epam.esm.repository.model.TagModel;
import com.epam.esm.service.converter.TagConverter;
import com.epam.esm.service.validation.CertificateValidation;
import com.epam.esm.service.validation.TagValidation;

@ExtendWith(MockitoExtension.class)
class TagServiceImplTest {
	private static final long ID = 1L;
	@Mock
	private TagValidation tagValidation;

	@Mock
	private TagRepository tagRepository;

	@Mock
	private TagConverter tagConverter;

	@Mock
	private CertificateRepository certificateRepository;

	@Mock
	private CertificateValidation certificateValidation;

	@InjectMocks
	private TagServiceImpl tagServiceImpl;

	@Test
	void testCreate() {
		TagModel tagModel = new TagModel();
		TagDto expected = new TagDto();

		Mockito.when(tagConverter.convertToModel(expected)).thenReturn(tagModel);
		Mockito.when(tagRepository.create(tagModel)).thenReturn(tagModel);
		Mockito.when(tagConverter.convertToDto(tagModel)).thenReturn(expected);

		TagDto actual = tagServiceImpl.create(expected);

		Assertions.assertEquals(expected, actual);

		Mockito.verify(tagRepository).create(tagModel);
		Mockito.verify(tagConverter).convertToDto(tagModel);
		Mockito.verify(tagConverter).convertToModel(expected);
		Mockito.verify(tagValidation).validateAllTagFields(Mockito.any());
	}

	@Test
	void testReadById() {
		TagModel tagModel = new TagModel();
		TagDto expected = new TagDto();

		Mockito.when(tagRepository.readById(Mockito.anyLong())).thenReturn(tagModel);
		Mockito.when(tagConverter.convertToDto(tagModel)).thenReturn(expected);

		Assertions.assertEquals(expected, tagServiceImpl.readById(Mockito.anyLong()));

		Mockito.verify(tagValidation).checkTagExistenceById(Mockito.anyLong());
		Mockito.verify(tagRepository).readById(Mockito.anyLong());
		Mockito.verify(tagConverter).convertToDto(tagModel);
	}

	@Test
	void testReadByCertificateId() {
		TagModel tagModel = new TagModel();
		List<TagModel> tagModels = Arrays.asList(tagModel);
		TagDto tagDto = new TagDto();
		List<TagDto> expected = Arrays.asList(tagDto);
		Mockito.when(tagRepository.readByCertificateId(Mockito.anyLong())).thenReturn(tagModels);
		Mockito.when(tagConverter.convertToDto(tagModel)).thenReturn(tagDto);

		Assertions.assertEquals(expected, tagServiceImpl.readByCertificateId(Mockito.anyLong()));

		Mockito.verify(certificateValidation).checkCertificateExistenceById(Mockito.anyLong());
		Mockito.verify(tagRepository).readByCertificateId(Mockito.anyLong());
		Mockito.verify(tagConverter).convertToDto(tagModel);
	}

	@Test
	void testReadAll() {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

		TagModel tagModel = new TagModel();
		tagModel.setId(ID);
		TagDto tagDto = new TagDto();
		tagDto.setId(ID);

		List<TagModel> tagModels = Arrays.asList(tagModel);

		List<TagDto> expected = Arrays.asList(tagDto);

		Mockito.when(tagRepository.readAll(params)).thenReturn(tagModels);
		Mockito.when(tagConverter.convertToDto(tagModel)).thenReturn(tagDto);

		Assertions.assertEquals(expected, tagServiceImpl.readAll(params));

		Mockito.verify(tagValidation).validateReadParams(params);
		Mockito.verify(tagRepository).readAll(params);
	}

	@Test
	void testDelete() {
		CertificateModel certificateModel = new CertificateModel();
		List<CertificateModel> certificateModels = Arrays.asList(certificateModel);
		Mockito.when(certificateRepository.readByTagId(ID)).thenReturn(certificateModels);
		tagServiceImpl.delete(ID);

		Mockito.verify(tagValidation).checkTagExistenceById(ID);
		Mockito.verify(tagRepository).delete(ID);
		Mockito.verify(certificateRepository).delete(Mockito.anyLong());
		Mockito.verify(certificateRepository).readByTagId(ID);
	}

}
