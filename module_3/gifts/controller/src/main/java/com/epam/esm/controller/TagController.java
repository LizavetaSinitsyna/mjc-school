package com.epam.esm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
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

import com.epam.esm.controller.assembler.TagViewAssembler;
import com.epam.esm.controller.converter.TagViewConverter;
import com.epam.esm.controller.view.TagView;
import com.epam.esm.dto.TagDto;
import com.epam.esm.service.TagService;

/**
 * Controller for working with tags.
 * 
 */
@RestController
@RequestMapping("/api/v1/tags")
public class TagController {

	private final TagService tagService;
	private final TagViewConverter tagConverter;
	private final PagedResourcesAssembler<TagDto> pagedResourcesAssembler;
	private final TagViewAssembler tagViewAssembler;

	@Autowired
	public TagController(TagService tagService, TagViewConverter tagConverter, TagViewAssembler tagViewAssembler,
			PagedResourcesAssembler<TagDto> pagedResourcesAssembler) {
		this.tagService = tagService;
		this.tagConverter = tagConverter;
		this.pagedResourcesAssembler = pagedResourcesAssembler;
		this.tagViewAssembler = tagViewAssembler;
	}

	/**
	 * Creates and saves the passed tag.
	 * 
	 * @param tagView the tag to be saved
	 * @return saved tag
	 */
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public TagView create(@RequestBody TagView tagView) {
		TagDto createdTag = tagService.create(tagConverter.convertToDto(tagView));
		return tagViewAssembler.toModel(createdTag);
	}

	/**
	 * Reads tag with passed id.
	 * 
	 * @param tagId id of the tag to be read
	 * @return tag with passed id
	 */
	@GetMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public TagView readById(@PathVariable long id) {
		TagDto tagDto = tagService.readById(id);
		return tagViewAssembler.toModel(tagDto);
	}

	/**
	 * Reads all tags according to the passed parameters.
	 * 
	 * @param params the parameters which define the choice of tags and their
	 *               ordering
	 * @return tags which meet passed parameters
	 */
	@GetMapping
	public ResponseEntity<PagedModel<TagView>> readAll(@RequestParam MultiValueMap<String, String> params) {
		Page<TagDto> tagPage = tagService.readAll(params);
		PagedModel<TagView> page = pagedResourcesAssembler.toModel(tagPage, tagViewAssembler);
		return new ResponseEntity<>(page, HttpStatus.OK);
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
