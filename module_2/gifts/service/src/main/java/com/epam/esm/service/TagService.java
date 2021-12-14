package com.epam.esm.service;

import java.util.List;

import org.springframework.util.MultiValueMap;

import com.epam.esm.dto.TagDto;

/**
 * 
 * Contains methods for working mostly with {@code TagDto} entity.
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
	 * Reads tag with passed id.
	 * 
	 * @param tagId id of tag to be read
	 * @return tag with passed id
	 */
	TagDto readById(long tagId);

	/**
	 * Reads all tags according to passed parameters.
	 * 
	 * @param params the parameters which define choice of tags and their ordering
	 * @return tags which meet passed parameters
	 */
	List<TagDto> readAll(MultiValueMap<String, String> params);

	/**
	 * Deletes tag with passed id.
	 * 
	 * @param tagId the id of tag to be deleted
	 * @return the number of deleted tags
	 */
	int delete(long tagId);

	/**
	 * Reads all tags for the certificate with passed id.
	 * 
	 * @param certificateId the id of certificate for which all tags are read
	 * @return tags for the certificate with passed id
	 */
	List<TagDto> readByCertificateId(long certificateId);

}
