package com.epam.esm.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.util.MultiValueMap;

import com.epam.esm.dto.TagDto;

/**
 * 
 * Contains methods for working mostly with tag entities.
 *
 */
public interface TagService {
	/**
	 * Creates and saves the passed tag.
	 * 
	 * @param tagDto the tag to be saved
	 * @return saved tag
	 */
	TagDto create(TagDto tagDto);

	/**
	 * Creates and saves the passed tags.
	 * 
	 * @param tagDtos the tags to be saved
	 * @return saved tags
	 */
	List<TagDto> createTags(List<TagDto> tagDtos);

	/**
	 * Reads tag with passed id.
	 * 
	 * @param tagId id of the tag to be read
	 * @return tag with passed id
	 */
	TagDto readById(long tagId);

	/**
	 * Reads all tags according to the passed parameters.
	 * 
	 * @param params the parameters which define the choice of tags and their
	 *               ordering
	 * @return tags which meet passed parameters
	 */
	Page<TagDto> readAll(MultiValueMap<String, String> params);

	/**
	 * Deletes tag with passed id.
	 * 
	 * @param tagId the id of the tag to be deleted
	 * @return the number of deleted tags
	 */
	int delete(long tagId);

	/**
	 * Finds the most widely used tag of a user with the highest cost of all orders.
	 * 
	 * @return the most widely used tag of a user with the highest cost of all
	 *         orders
	 */
	TagDto readPopularTagByMostProfitableUser();
}
