package com.epam.esm.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epam.esm.dto.TagDto;
import com.epam.esm.repository.TagRepository;
import com.epam.esm.repository.model.TagModel;
import com.epam.esm.service.TagService;
import com.epam.esm.service.converter.TagConverter;

@Service
public class TagServiceImpl implements TagService {

	@Autowired
	private TagRepository tagRepository;

	@Override
	public TagDto create(TagDto tagDto) {

		TagModel createdTagModel = tagRepository.create(TagConverter.convertDtoToModel(tagDto));
		TagDto createdTag = TagConverter.convertModelToDTO(createdTagModel);
		return createdTag;
	}

	@Override
	public TagDto read(long tagId) {
		TagDto tagDto = TagConverter.convertModelToDTO(tagRepository.readById(tagId));

		return tagDto;
	}

	@Override
	public List<TagDto> readAll(Map<String, String> filterParams) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int delete(long tagId) {
		// TODO Auto-generated method stub
		return 0;
	}

}
