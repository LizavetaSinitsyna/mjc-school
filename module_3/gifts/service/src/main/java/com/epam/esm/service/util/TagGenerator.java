package com.epam.esm.service.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.epam.esm.dto.TagDto;
import com.epam.esm.exception.GeneratorException;
import com.epam.esm.service.TagService;

@Component
public class TagGenerator {
	private final TagService tagService;

	@Autowired
	public TagGenerator(TagService tagService) {
		this.tagService = tagService;
	}

	public List<TagDto> generateTags(int amount) {
		List<TagDto> tagDtosToCreate = new ArrayList<>(amount);
		ClassPathResource res = new ClassPathResource(GeneratorConstant.TAG_NAME_FILE_PATH);
		int lineCounter = 0;
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(res.getInputStream()))) {
			String name;
			while ((name = reader.readLine()) != null && lineCounter < amount) {
				TagDto tagDto = new TagDto();
				tagDto.setName(name);
				tagDtosToCreate.add(tagDto);
				++lineCounter;
			}
			if (lineCounter != amount) {
				throw new GeneratorException(GeneratorConstant.NOT_ENOUGH_DATA_EXCEPTION);
			}
		} catch (IOException e) {
			throw new GeneratorException(e.getMessage());
		}
		return tagService.createTags(tagDtosToCreate);
	}
}
