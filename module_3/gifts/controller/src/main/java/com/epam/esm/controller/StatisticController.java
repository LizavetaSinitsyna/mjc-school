package com.epam.esm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.epam.esm.controller.assembler.TagViewAssembler;
import com.epam.esm.controller.view.TagView;
import com.epam.esm.dto.TagDto;
import com.epam.esm.service.TagService;

/**
 * Controller for working with statistics.
 * 
 */
@RestController
@RequestMapping("/api/v1/statistics")
public class StatisticController {

	private final TagService tagService;
	private final TagViewAssembler tagViewAssembler;

	@Autowired
	public StatisticController(TagService tagService, TagViewAssembler tagViewAssembler) {
		this.tagService = tagService;
		this.tagViewAssembler = tagViewAssembler;
	}

	/**
	 * Selects the most widely used tag of a user with the highest cost of all
	 * orders.
	 * 
	 * @return the most widely used tag of a user with the highest cost of all
	 *         orders
	 */
	@GetMapping("/tags/popular")
	@ResponseStatus(HttpStatus.OK)
	public TagView readPopularTagByMostCostlyUser() {
		TagDto popularTag = tagService.readPopularTagByMostProfitableUser();
		return tagViewAssembler.toModel(popularTag);
	}
}
