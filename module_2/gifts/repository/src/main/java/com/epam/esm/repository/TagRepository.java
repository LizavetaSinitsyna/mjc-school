package com.epam.esm.repository;

import java.util.List;
import java.util.Map;

import com.epam.esm.repository.model.TagModel;

public interface TagRepository {
	TagModel create(TagModel tagModel);

	TagModel readById(long tagId);

	List<TagModel> readAll(Map<String, String> filterParams);

	int delete(long tagId);

	List<TagModel> readByCertificateId(long certificateId);

	TagModel readByTagName(String tagName);

	int saveTagsForCertificate(long certificateId, List<TagModel> tagModels);

	TagModel restore(TagModel tagModel);

	int deleteAllTagsForCertificate(long certificateId);
}
