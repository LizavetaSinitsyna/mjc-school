package com.epam.esm.controller.assembler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import com.epam.esm.controller.HateoasUtil;
import com.epam.esm.controller.TagController;
import com.epam.esm.controller.converter.TagViewConverter;
import com.epam.esm.controller.view.TagView;
import com.epam.esm.dto.TagDto;

@Component
public class TagViewAssembler extends RepresentationModelAssemblerSupport<TagDto, TagView> {

	private final TagViewConverter tagConverter;

	@Autowired
	public TagViewAssembler(TagViewConverter tagConverter) {
		super(TagController.class, TagView.class);
		this.tagConverter = tagConverter;

	}

	@Override
	public TagView toModel(TagDto entity) {
		TagView tagView = tagConverter.convertToView(entity);
		HateoasUtil.addLinksToTag(tagView);
		return tagView;
	}
}
