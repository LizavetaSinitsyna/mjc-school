package com.epam.esm.repository;

import java.util.List;
import java.util.Optional;

import com.epam.esm.repository.model.TagModel;

/**
 * 
 * Contains methods for working mostly with {@code TagModel} entity.
 *
 */
public interface TagRepository extends GeneralRepository<TagModel> {
	/**
	 * Checks whether the tag with passed id exists.
	 * 
	 * @param tagId the id of the tag to be checked
	 * @return {@code true} if the the tag with passed id already exists and
	 *         {@code false} otherwise
	 */
	boolean tagExistsById(long tagId);

	/**
	 * Reads tag with passed name.
	 * 
	 * @param name the name of the tag to be read
	 * @return tag with passed name
	 */
	Optional<TagModel> findByName(String name);

	/**
	 * Checks whether tag with passed name exists.
	 * 
	 * @param tagName the name of the tag to be checked
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

	/**
	 * Deletes tag with passed id.
	 * 
	 * @param id the id of the tag to be deleted
	 * @return the number of deleted tags
	 */
	int delete(long id);

	/**
	 * Finds the most widely used tag of a user with the highest cost of all orders.
	 * 
	 * @return the most widely used tag of a user with the highest cost of all
	 *         orders
	 */
	Optional<TagModel> findPopularTagByMostProfitableUser();

	/**
	 * Saves the passed tags.
	 * 
	 * @param tagModels the tags to be saved
	 * @return saved tags
	 */
	List<TagModel> saveTags(List<TagModel> tagModels);
}
