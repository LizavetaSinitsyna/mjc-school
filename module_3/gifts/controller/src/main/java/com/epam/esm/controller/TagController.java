package com.epam.esm.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.epam.esm.dto.TagDto;
import com.epam.esm.service.TagService;

/**
 * Controller for working with tags.
 * 
 */
@RestController
@RequestMapping("/api/v1/tags")
public class TagController {

	private TagService tagService;

	@Autowired
	public TagController(TagService tagService) {
		this.tagService = tagService;
	}

	/**
	 * Creates and saves the passed tag.
	 * 
	 * @param tagDto the tag to be saved
	 * @return saved tag
	 */
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public TagDto create(@RequestBody TagDto tagDto) {
		return tagService.create(tagDto);
	}

	/**
	 * Reads tag with passed id.
	 * 
	 * @param tagId id of tag to be read
	 * @return tag with passed id
	 */
	@GetMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public TagDto readById(@PathVariable long id) {
		return tagService.readById(id);
	}

	/**
	 * Reads all tags according to passed parameters.
	 * 
	 * @param params the parameters which define the choice of tags and their
	 *               ordering
	 * @return tags which meet passed parameters
	 */
	@GetMapping
	public ResponseEntity<List<TagDto>> readAll(@RequestParam MultiValueMap<String, String> params) {
		List<TagDto> tags = tagService.readAll(params);
		if (tags.isEmpty()) {
			return new ResponseEntity<>(tags, HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<>(tags, HttpStatus.OK);
		}
	}

	/**
	 * Deletes tag with passed id.
	 * 
	 * @param tagId the id of tag to be deleted
	 */
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable long id) {
		tagService.delete(id);
	}
}
