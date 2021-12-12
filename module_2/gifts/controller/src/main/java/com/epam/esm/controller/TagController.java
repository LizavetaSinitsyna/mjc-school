package com.epam.esm.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

@RestController
@RequestMapping("/v1/tags")
public class TagController {
	@Autowired
	private TagService tagService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public TagDto create(@RequestBody TagDto tagDto) {
		return tagService.create(tagDto);
	}

	@GetMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public TagDto readById(@PathVariable long id) {
		return tagService.readById(id);
	}
	
	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public List<TagDto> readAll(@RequestParam MultiValueMap<String, String> params) {
		return tagService.readAll(params);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable long id) {
		tagService.delete(id);
	}
}