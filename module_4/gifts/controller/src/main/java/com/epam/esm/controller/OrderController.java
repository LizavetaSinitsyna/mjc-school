package com.epam.esm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.epam.esm.controller.converter.OrderViewConverter;
import com.epam.esm.controller.assembler.OrderViewAssembler;
import com.epam.esm.controller.converter.OrderDataViewConverter;
import com.epam.esm.controller.view.OrderDataView;
import com.epam.esm.controller.view.OrderView;
import com.epam.esm.dto.OrderDataDto;
import com.epam.esm.dto.OrderDto;
import com.epam.esm.service.OrderService;

/**
 * Controller for working with orders.
 * 
 */
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

	private final OrderService orderService;
	private final OrderViewConverter orderConverter;
	private final OrderDataViewConverter orderDataConverter;
	private final PagedResourcesAssembler<OrderDto> pagedResourcesAssembler;
	private final OrderViewAssembler orderViewAssembler;

	@Autowired
	public OrderController(OrderService orderService, OrderViewConverter orderConverter,
			OrderDataViewConverter orderDataConverter, PagedResourcesAssembler<OrderDto> pagedResourcesAssembler,
			OrderViewAssembler orderViewAssembler) {
		this.orderService = orderService;
		this.orderConverter = orderConverter;
		this.orderDataConverter = orderDataConverter;
		this.pagedResourcesAssembler = pagedResourcesAssembler;
		this.orderViewAssembler = orderViewAssembler;
	}

	/**
	 * Reads order with passed id.
	 * 
	 * @param orderId id of the order to be read
	 * @return the order with passed id
	 */
	@GetMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	@PreAuthorize("hasRole('ADMIN')")
	public OrderView readById(@PathVariable long id) {
		OrderDto orderDto = orderService.readById(id);
		return orderViewAssembler.toModel(orderDto);
	}

	/**
	 * Reads all orders according to the passed parameters.
	 * 
	 * @param params the parameters which define the choice of orders and their
	 *               ordering
	 * @return orders which meet passed parameters
	 */
	@GetMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<PagedModel<OrderView>> readAll(@RequestParam MultiValueMap<String, String> params) {
		Page<OrderDto> orderPage = orderService.readAll(params);
		PagedModel<OrderView> page = pagedResourcesAssembler.toModel(orderPage, orderViewAssembler);
		return new ResponseEntity<>(page, HttpStatus.OK);
	}

	/**
	 * Reads all orders for the specified user according to the passed parameters.
	 * 
	 * @param userId id of the user whose orders should be read
	 * @param params the parameters which define the choice of orders and their
	 *               ordering
	 * @return orders for specified user which meet the passed parameters
	 */
	@GetMapping("/users/{userId}")
	@PreAuthorize("hasRole('ADMIN') or #userId == principal.id")
	public ResponseEntity<PagedModel<OrderView>> readByUserId(@PathVariable long userId,
			@RequestParam MultiValueMap<String, String> params) {
		Page<OrderDto> orderPage = orderService.readAllByUserId(userId, params);
		PagedModel<OrderView> page = pagedResourcesAssembler.toModel(orderPage, orderViewAssembler);
		return new ResponseEntity<>(page, HttpStatus.OK);
	}

	/**
	 * Reads information about the order with passed id for the specified user.
	 * 
	 * @param userId  id of the user whose order should be read
	 * @param orderId id of the order to be read
	 * @return information about the order with passed id for the specified user
	 */
	@GetMapping("/{orderId}/users/{userId}")
	@PreAuthorize("hasRole('ADMIN') or #userId == principal.id")
	public OrderDataView readOrderDataByUserId(@PathVariable long userId, @PathVariable long orderId) {
		OrderDataDto orderDataDto = orderService.readOrderDataByUserId(userId, orderId);
		OrderDataView orderDataView = orderDataConverter.convertToView(orderDataDto);
		HateoasUtil.addLinksToOrderData(orderId, orderDataView);
		return orderDataView;
	}

	/**
	 * Creates and saves passed order.
	 * 
	 * @param userId   id of the user whose order will be saved
	 * @param orderDto the order to be saved
	 * @return saved order
	 */
	@PostMapping("/users/{userId}")
	@PreAuthorize("#userId == principal.id")
	public OrderView create(@PathVariable long userId, @RequestBody OrderView orderView) {
		OrderDto createdOrderDto = orderService.create(userId, orderConverter.convertToDto(orderView));
		OrderView createdOrderView = orderConverter.convertToView(createdOrderDto);
		HateoasUtil.addLinksToOrder(createdOrderView);
		return createdOrderView;
	}
}
