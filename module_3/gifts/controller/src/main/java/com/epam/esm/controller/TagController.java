package com.epam.esm.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.ArrayList;
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

import com.epam.esm.controller.converter.PageViewConverter;
import com.epam.esm.controller.converter.TagViewConverter;
import com.epam.esm.controller.view.PageView;
import com.epam.esm.controller.view.TagView;
import com.epam.esm.dto.PageDto;
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
	private final PageViewConverter<TagView, TagDto> pageConverter;
	private final TagViewConverter tagConverter;

	@Autowired
	public TagController(TagService tagService, TagViewConverter tagConverter,
			PageViewConverter<TagView, TagDto> pageConverter) {
		this.tagService = tagService;
		this.pageConverter = pageConverter;
		this.tagConverter = tagConverter;
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
		TagView createdTagView = tagConverter.convertToView(createdTag);
		HateoasUtil.addLinksToTag(createdTagView);
		return createdTagView;
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
		TagView tagView = tagConverter.convertToView(tagDto);
		HateoasUtil.addLinksToTag(tagView);
		return tagView;
	}

	/**
	 * Reads all tags according to the passed parameters.
	 * 
	 * @param params the parameters which define the choice of tags and their
	 *               ordering
	 * @return tags which meet passed parameters
	 */
	@GetMapping
	public ResponseEntity<PageView<TagView>> readAll(@RequestParam MultiValueMap<String, String> params) {
		PageDto<TagDto> tagPage = tagService.readAll(params);
		List<TagDto> tags = tagPage.getEntities();

		if (tagPage == null || tags == null || tags.isEmpty()) {
			return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
		} else {
			List<TagView> tagsView = new ArrayList<>(tags.size());
			tags.forEach(tagDto -> tagsView.add(tagConverter.convertToView(tagDto)));
			PageView<TagView> tagPageView = pageConverter.convertToView(tagPage, tagsView);
			tagsView.forEach(tagView -> HateoasUtil.addLinksToTag(tagView));
			HateoasUtil.addLinksToPage(tagPageView, linkTo(methodOn(TagController.class).readAll(params)));
			return new ResponseEntity<>(tagPageView, HttpStatus.OK);
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
