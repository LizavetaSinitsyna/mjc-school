package com.epam.esm.repository;

import java.util.List;

import com.epam.esm.repository.model.TagModel;

public interface TagRepository extends GeneralRepository<TagModel> {

	List<TagModel> readByCertificateId(long certificateId);

	int saveTagsForCertificate(long certificateId, List<TagModel> tagModels);

	TagModel restore(TagModel tagModel);

	int deleteAllTagsForCertificate(long certificateId);

	boolean tagExistsById(long tagId);

	boolean tagExistsByName(String tagName);
}
