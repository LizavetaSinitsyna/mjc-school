package com.epam.esm.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.epam.esm.dto.TagDto;
import com.epam.esm.exception.ValidationException;
import com.epam.esm.repository.CertificateRepository;
import com.epam.esm.repository.TagRepository;
import com.epam.esm.repository.model.CertificateModel;
import com.epam.esm.repository.model.EntityConstant;
import com.epam.esm.repository.model.TagModel;
import com.epam.esm.service.ServiceConstant;
import com.epam.esm.service.TagService;
import com.epam.esm.service.converter.PageConverter;
import com.epam.esm.service.converter.TagConverter;
import com.epam.esm.service.validation.TagValidation;

class TagServiceImplTest {
	private static final Long TAG_ID_1 = 1L;
	private static final Long CERTIFIATE_ID_1 = 1L;
	private static final Long INVALID_ID = -1L;

	private static TagValidation tagValidation;
	private static TagConverter tagConverter;
	private static PageConverter<TagDto, TagModel> pageConverter;
	private static TagService tagService;
	private TagRepository tagRepository;
	private CertificateRepository certificateRepository;
	private Page<TagModel> tagModelsPage;
	private Page<TagDto> tagDtosPage;

	private TagModel tagModel1;
	private TagDto tagDto1;

	@BeforeAll
	public static void init() {
		tagValidation = new TagValidation();
		tagConverter = new TagConverter();
		pageConverter = new PageConverter<>();
	}

	@BeforeEach
	public void setUp() {
		certificateRepository = Mockito.mock(CertificateRepository.class);
		tagRepository = Mockito.mock(TagRepository.class);
		tagService = new TagServiceImpl(certificateRepository, tagRepository, tagConverter, tagValidation,
				pageConverter);

		tagModel1 = new TagModel();
		tagModel1.setName("food");
		tagModel1.setDeleted(false);

		tagDto1 = new TagDto();
		tagDto1.setName("food");

		List<TagDto> tagDtos = new ArrayList<>();
		tagDtos.add(tagDto1);

		List<TagModel> tagModels = new ArrayList<>();
		tagModels.add(tagModel1);

		Pageable pageable = PageRequest.of(ServiceConstant.DEFAULT_PAGE_NUMBER, ServiceConstant.DEFAULT_LIMIT);
		tagModelsPage = new PageImpl<>(tagModels, pageable, tagModels.size());
		tagDtosPage = new PageImpl<>(tagDtos, pageable, tagDtos.size());
	}

	@Test
	void testCreate() {
		TagDto expected = tagDto1;

		Mockito.when(tagRepository.save(Mockito.any())).thenReturn(tagModel1);
		Mockito.when(tagRepository.tagExistsByName(Mockito.any())).thenReturn(false);

		TagDto actual = tagService.create(expected);

		Assertions.assertEquals(expected, actual);

		Mockito.verify(tagRepository).save(Mockito.any());
		Mockito.verify(tagRepository).tagExistsByName(Mockito.any());
	}

	@Test
	void testCreateWithDublicatedName() {
		Mockito.when(tagRepository.tagExistsByName(Mockito.any())).thenReturn(true);
		Assertions.assertThrows(ValidationException.class, () -> {
			tagService.create(tagDto1);
		});
	}

	@Test
	void testCreateTags() {
		List<TagDto> expected = new ArrayList<>();
		expected.add(tagDto1);

		Mockito.when(tagRepository.saveTags(Mockito.any())).thenReturn(Arrays.asList(tagModel1));
		Mockito.when(tagRepository.tagExistsByName(Mockito.any())).thenReturn(false);

		List<TagDto> actual = tagService.createTags(expected);

		Assertions.assertEquals(expected, actual);

		Mockito.verify(tagRepository).saveTags(Mockito.any());
		Mockito.verify(tagRepository).tagExistsByName(Mockito.any());
	}

	@Test
	void testReadById() {
		TagDto expected = tagDto1;

		Mockito.when(tagRepository.findById(TAG_ID_1)).thenReturn(Optional.of(tagModel1));

		TagDto actual = tagService.readById(TAG_ID_1);
		Assertions.assertEquals(expected, actual);

		Mockito.verify(tagRepository).findById(Mockito.anyLong());
	}

	@Test
	void testReadByIdWithInvalidId() {
		Assertions.assertThrows(ValidationException.class, () -> {
			tagService.readById(INVALID_ID);
		});
	}

	@Test
	void testReadAll() {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

		Page<TagDto> expected = tagDtosPage;

		Mockito.when(tagRepository.findAll(ServiceConstant.DEFAULT_PAGE_NUMBER, ServiceConstant.DEFAULT_LIMIT))
				.thenReturn(tagModelsPage);

		Page<TagDto> actual = tagService.readAll(params);
		Assertions.assertEquals(expected, actual);

		Mockito.verify(tagRepository).findAll(ServiceConstant.DEFAULT_PAGE_NUMBER, ServiceConstant.DEFAULT_LIMIT);
	}

	@Test
	void testReadAllWithInvalidReadParam() {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.put(EntityConstant.SEARCH, Arrays.asList("family"));

		Assertions.assertThrows(ValidationException.class, () -> {
			tagService.readAll(params);
		});
	}

	@Test
	void testDelete() {
		CertificateModel certificateModel = new CertificateModel();
		certificateModel.setId(CERTIFIATE_ID_1);
		List<CertificateModel> certificateModels = Arrays.asList(certificateModel);

		Mockito.when(certificateRepository.readByTagId(TAG_ID_1)).thenReturn(certificateModels);
		Mockito.when(tagRepository.delete(TAG_ID_1)).thenReturn(1);
		Mockito.when(tagRepository.tagExistsById(TAG_ID_1)).thenReturn(true);

		tagService.delete(TAG_ID_1);

		Mockito.verify(tagRepository).delete(TAG_ID_1);
		Mockito.verify(certificateRepository).delete(TAG_ID_1);
		Mockito.verify(certificateRepository).readByTagId(TAG_ID_1);
	}

	@Test
	void testDeleteWithInvalidId() {
		Assertions.assertThrows(ValidationException.class, () -> {
			tagService.delete(INVALID_ID);
		});
	}

	@Test
	void testReadPopularTagByMostProfitableUser() {
		Mockito.when(tagRepository.findPopularTagByMostProfitableUser()).thenReturn(Optional.of(tagModel1));
		TagDto actual = tagService.readPopularTagByMostProfitableUser();
		Assertions.assertEquals(tagDto1, actual);
		Mockito.verify(tagRepository).findPopularTagByMostProfitableUser();
	}
}
