package com.epam.esm.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.epam.esm.dto.TagDto;
import com.epam.esm.dto.UserDto;
import com.epam.esm.service.TagService;
import com.epam.esm.service.UserService;

/**
 * Controller for working with statistics.
 * 
 */
@RestController
@RequestMapping("/api/v1/statistics")
public class StatisticController {

	private TagService tagService;

	@Autowired
	public StatisticController(TagService tagService) {
		this.tagService = tagService;
	}

	@GetMapping("/user/{userId}/popular/tag")
	@ResponseStatus(HttpStatus.OK)
	public TagDto readPopularTagByUserId(@PathVariable long id) {
		return null; //tagService.readPopularTagByUserId(id);
	}
}
