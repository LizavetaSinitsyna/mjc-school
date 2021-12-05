package com.epam.esm.service;

import java.util.List;
import java.util.Map;

import com.epam.esm.dto.TagDto;

public interface TagService {
	TagDto create(TagDto tagDTO);

	TagDto read(long tagId);

	List<TagDto> readAll(Map<String, String> filterParams);

	int delete(long tagId);

}
