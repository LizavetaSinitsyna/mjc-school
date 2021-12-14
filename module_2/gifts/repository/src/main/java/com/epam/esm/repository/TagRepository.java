package com.epam.esm.repository;

import java.util.List;

import com.epam.esm.repository.model.TagModel;

/**
 * 
 * Contains methods for working mostly with {@code TagModel} entity.
 *
 */
public interface TagRepository extends GeneralRepository<TagModel> {
	/**
	 * Reads tags by passed certificate id.
	 * 
	 * @param certificateId the id of certificate for tags reading
	 * @return tags for certificate with passed id
	 */
	List<TagModel> readByCertificateId(long certificateId);

	/**
	 * Saves tags for certificate.
	 * 
	 * @param certificateId the id of certificate for which tags should be saved
	 * @param tagModels     tags to be saved
	 * @return amount of saved tags
	 */
	int saveTagsForCertificate(long certificateId, List<TagModel> tagModels);

	/**
	 * Restores deleted tag.
	 * 
	 * @param tagModel the tag to be restored
	 * @return restored tag
	 */
	TagModel restore(TagModel tagModel);

	/**
	 * Deletes all tags for the certificate.
	 * 
	 * @param certificateId the id of certificate for which tags should be deleted
	 * @return amount of deleted tags
	 */
	int deleteAllTagsForCertificate(long certificateId);

	/**
	 * Checks whether tag with passed id exists.
	 * 
	 * @param tagId the id of tag to be checked
	 * @return {@code true} if the the tag with passed id already exists and
	 *         {@code false} otherwise
	 */
	boolean tagExistsById(long tagId);

	/**
	 * Checks whether tag with passed name exists.
	 * 
	 * @param tagName the name of tag to be checked
	 * @return {@code true} if the the tag with passed name already exists and
	 *         {@code false} otherwise
	 */
	boolean tagExistsByName(String tagName);
}
