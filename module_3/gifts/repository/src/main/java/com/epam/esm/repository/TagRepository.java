package com.epam.esm.repository;

import com.epam.esm.repository.model.TagModel;

/**
 * 
 * Contains methods for working mostly with {@code TagModel} entity.
 *
 */
public interface TagRepository extends GeneralRepository<TagModel> {

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

	/**
	 * Restores deleted tag.
	 * 
	 * @param tagModel the tag to be restored
	 * @return restored tag
	 */
	int restore(long tagId);
}
