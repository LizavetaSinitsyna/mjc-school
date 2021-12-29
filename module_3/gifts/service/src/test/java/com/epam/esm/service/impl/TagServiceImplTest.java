package com.epam.esm.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.epam.esm.dto.TagDto;
import com.epam.esm.exception.ValidationException;
import com.epam.esm.repository.CertificateRepository;
import com.epam.esm.repository.TagRepository;
import com.epam.esm.repository.model.CertificateModel;
import com.epam.esm.repository.model.TagModel;
import com.epam.esm.service.TagService;
import com.epam.esm.service.converter.TagConverter;
import com.epam.esm.service.validation.TagValidation;

@ExtendWith(MockitoExtension.class)
class TagServiceImplTest {
	private static TagValidation tagValidation;
	private static TagConverter tagConverter;
	private TagRepository tagRepository;

	private CertificateRepository certificateRepository;

	private static TagService tagServiceImpl;

	private static final Long TAG_ID_1 = 1L;
	private static final Long CERTIFIATE_ID_1 = 1L;
	private static final Long INVALID_ID = -1L;
	private static final int OFFSET = 0;
	private static final int LIMIT = 10;
	private TagModel tagModel1;
	private TagDto tagDto1;

	@BeforeAll
	public static void init() {
		tagValidation = new TagValidation();
		tagConverter = new TagConverter();
	}

	@BeforeEach
	public void setUp() {
		certificateRepository = Mockito.mock(CertificateRepository.class);
		tagRepository = Mockito.mock(TagRepository.class);
		tagServiceImpl = new TagServiceImpl(certificateRepository, tagRepository, tagConverter, tagValidation);

		tagModel1 = new TagModel();
		tagModel1.setName("food");
		tagModel1.setDeleted(false);

		tagDto1 = new TagDto();
		tagDto1.setName("food");

	}

	@Test
	void testCreate() {
		TagDto expected = tagDto1;

		Mockito.when(tagRepository.save(Mockito.any())).thenReturn(tagModel1);
		Mockito.when(tagRepository.tagExistsByName(Mockito.any())).thenReturn(false);

		TagDto actual = tagServiceImpl.create(expected);

		Assertions.assertEquals(expected, actual);

		Mockito.verify(tagRepository).save(Mockito.any());
		Mockito.verify(tagRepository).tagExistsByName(Mockito.any());

	}

	@Test
	void testCreateWithDublicatedName() {
		Mockito.when(tagRepository.tagExistsByName(Mockito.any())).thenReturn(true);
		Assertions.assertThrows(ValidationException.class, () -> {
			tagServiceImpl.create(tagDto1);
		});

	}

	@Test
	void testReadById() {
		TagDto expected = tagDto1;

		Mockito.when(tagRepository.findById(TAG_ID_1)).thenReturn(Optional.of(tagModel1));

		TagDto actual = tagServiceImpl.readById(TAG_ID_1);
		Assertions.assertEquals(expected, actual);

		Mockito.verify(tagRepository).findById(Mockito.anyLong());
	}

	@Test
	void testReadByIdWithInvalidId() {
		Assertions.assertThrows(ValidationException.class, () -> {
			tagServiceImpl.readById(INVALID_ID);
		});
	}

	@Test
	void testReadAll() {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		List<TagModel> tagModels = Arrays.asList(tagModel1);

		List<TagDto> expected = Arrays.asList(tagDto1);

		Mockito.when(tagRepository.findAll(OFFSET, LIMIT)).thenReturn(tagModels);

		List<TagDto> actual = tagServiceImpl.readAll(params);
		Assertions.assertEquals(expected, actual);

		Mockito.verify(tagRepository).findAll(OFFSET, LIMIT);
	}

	@Test
	void testDelete() {
		CertificateModel certificateModel = new CertificateModel();
		certificateModel.setId(CERTIFIATE_ID_1);
		List<CertificateModel> certificateModels = Arrays.asList(certificateModel);
		Mockito.when(certificateRepository.readByTagId(TAG_ID_1)).thenReturn(certificateModels);
		Mockito.when(tagRepository.delete(TAG_ID_1)).thenReturn(1);
		Mockito.when(tagRepository.tagExistsById(TAG_ID_1)).thenReturn(true);
		tagServiceImpl.delete(TAG_ID_1);

		Mockito.verify(tagRepository).delete(TAG_ID_1);
		Mockito.verify(certificateRepository).delete(TAG_ID_1);
		Mockito.verify(certificateRepository).readByTagId(TAG_ID_1);
	}

	@Test
	void testDeleteWithInvalidId() {
		Assertions.assertThrows(ValidationException.class, () -> {
			tagServiceImpl.delete(INVALID_ID);
		});
	}

	@Test
	void testReadPopularTagByMostProfitableUser() {
		Mockito.when(tagRepository.findPopularTagByMostProfitableUser()).thenReturn(Optional.of(tagModel1));
		TagDto actual = tagServiceImpl.readPopularTagByMostProfitableUser();
		Assertions.assertEquals(tagDto1, actual);
		Mockito.verify(tagRepository).findPopularTagByMostProfitableUser();
	}

}
