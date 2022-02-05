package com.epam.esm.controller.assembler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import com.epam.esm.controller.HateoasUtil;
import com.epam.esm.controller.OrderController;
import com.epam.esm.controller.converter.OrderViewConverter;
import com.epam.esm.controller.view.OrderView;
import com.epam.esm.dto.OrderDto;

@Component
public class OrderViewAssembler extends RepresentationModelAssemblerSupport<OrderDto, OrderView> {

	private final OrderViewConverter orderConverter;

	@Autowired
	public OrderViewAssembler(OrderViewConverter orderConverter) {
		super(OrderController.class, OrderView.class);
		this.orderConverter = orderConverter;

	}

	@Override
	public OrderView toModel(OrderDto entity) {
		OrderView orderView = orderConverter.convertToView(entity);
		HateoasUtil.addLinksToOrder(orderView);
		return orderView;
	}
}
