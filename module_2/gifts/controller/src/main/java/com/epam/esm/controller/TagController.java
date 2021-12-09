package com.epam.esm.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
	public TagDto read(@PathVariable long id) {
		return tagService.read(id);
	}
	
	//not implemented yet
	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public List<TagDto> readAll(@RequestParam Map<String, String> filterParams) {
		return tagService.readAll(filterParams);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable long id) {
		tagService.delete(id);
	}
}
