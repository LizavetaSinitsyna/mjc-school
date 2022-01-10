package com.epam.esm.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import com.epam.esm.controller.converter.OrderDataViewConverter;
import com.epam.esm.controller.converter.PageViewConverter;
import com.epam.esm.controller.view.OrderDataView;
import com.epam.esm.controller.view.OrderView;
import com.epam.esm.controller.view.PageView;
import com.epam.esm.dto.OrderDataDto;
import com.epam.esm.dto.OrderDto;
import com.epam.esm.dto.PageDto;
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
	private final PageViewConverter<OrderView, OrderDto> pageConverter;

	@Autowired
	public OrderController(OrderService orderService, PageViewConverter<OrderView, OrderDto> pageConverter,
			OrderViewConverter orderConverter, OrderDataViewConverter orderDataConverter) {
		this.orderService = orderService;
		this.orderConverter = orderConverter;
		this.orderDataConverter = orderDataConverter;
		this.pageConverter = pageConverter;
	}

	/**
	 * Reads order with passed id.
	 * 
	 * @param orderId id of the order to be read
	 * @return the order with passed id
	 */
	@GetMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public OrderView readById(@PathVariable long id) {
		OrderDto orderDto = orderService.readById(id);
		OrderView orderView = orderConverter.convertToView(orderDto);
		HateoasUtil.addLinksToOrder(orderView);
		return orderView;
	}

	/**
	 * Reads all orders according to the passed parameters.
	 * 
	 * @param params the parameters which define the choice of orders and their
	 *               ordering
	 * @return orders which meet passed parameters
	 */
	@GetMapping
	public ResponseEntity<PageView<OrderView>> readAll(@RequestParam MultiValueMap<String, String> params) {
		PageDto<OrderDto> orderPage = orderService.readAll(params);
		List<OrderDto> orders = orderPage.getEntities();
		if (orderPage == null || orders == null || orders.isEmpty()) {
			return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
		} else {
			List<OrderView> ordersView = new ArrayList<>(orders.size());
			orders.forEach(orderDto -> ordersView.add(orderConverter.convertToView(orderDto)));
			PageView<OrderView> orderPageView = pageConverter.convertToView(orderPage, ordersView);
			ordersView.forEach(orderView -> HateoasUtil.addLinksToOrder(orderView));
			HateoasUtil.addLinksToPage(orderPageView, linkTo(methodOn(CertificateController.class).readAll(params)));
			orders.forEach(orderDto -> HateoasUtil.addLinksToOrder(orderConverter.convertToView(orderDto)));
			HateoasUtil.addLinksToPage(orderPageView, linkTo(methodOn(OrderController.class).readAll(params)));
			return new ResponseEntity<>(orderPageView, HttpStatus.OK);
		}
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
	public ResponseEntity<PageView<OrderView>> readByUserId(@PathVariable long userId,
			@RequestParam MultiValueMap<String, String> params) {
		PageDto<OrderDto> orderPage = orderService.readAllByUserId(userId, params);
		List<OrderDto> orders = orderPage.getEntities();
		if (orderPage == null || orders == null || orders.isEmpty()) {
			return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
		} else {
			List<OrderView> ordersView = new ArrayList<>(orders.size());
			orders.forEach(orderDto -> ordersView.add(orderConverter.convertToView(orderDto)));
			PageView<OrderView> orderPageView = pageConverter.convertToView(orderPage, ordersView);
			ordersView.forEach(orderView -> HateoasUtil.addLinksToOrder(orderView));
			HateoasUtil.addLinksToPage(orderPageView, linkTo(methodOn(CertificateController.class).readAll(params)));
			orders.forEach(orderDto -> HateoasUtil.addLinksToOrder(orderConverter.convertToView(orderDto)));
			HateoasUtil.addLinksToPage(orderPageView, linkTo(methodOn(OrderController.class).readAll(params)));
			return new ResponseEntity<>(orderPageView, HttpStatus.OK);
		}
	}

	/**
	 * Reads information about the order with passed id for the specified user.
	 * 
	 * @param userId  id of the user whose order should be read
	 * @param orderId id of the order to be read
	 * @return information about the order with passed id for the specified user
	 */
	@GetMapping("/{orderId}/users/{userId}")
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
	public OrderView create(@PathVariable long userId, @RequestBody OrderView orderView) {
		OrderDto createdOrderDto = orderService.create(userId, orderConverter.convertToDto(orderView));
		OrderView createdOrderView = orderConverter.convertToView(createdOrderDto);
		HateoasUtil.addLinksToOrder(createdOrderView);
		return createdOrderView;
	}
}
