package com.epam.esm.controller;

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

	private OrderService orderService;

	@Autowired
	public OrderController(OrderService orderService) {
		this.orderService = orderService;
	}

	/**
	 * Reads order with passed id.
	 * 
	 * @param orderId id of order to be read
	 * @return order with passed id
	 */
	@GetMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public OrderDto readById(@PathVariable long id) {
		return orderService.readById(id);
	}
	
	/**
	 * Reads all orders according to passed parameters.
	 * 
	 * @param params the parameters which define the choice of orders and their ordering
	 * @return orders which meet passed parameters
	 */
	@GetMapping
	public ResponseEntity<List<OrderDto>> readAll(@RequestParam MultiValueMap<String, String> params) {
		List<OrderDto> orders = orderService.readAll(params);
		if (orders == null || orders.isEmpty()) {
			return new ResponseEntity<>(orders, HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<>(orders, HttpStatus.OK);
		}
	}

	@GetMapping("/user/{userId}")
	public ResponseEntity<List<OrderDto>> readByUserId(@PathVariable long userId,
			@RequestParam MultiValueMap<String, String> params) {
		List<OrderDto> orders = orderService.readAllByUserId(userId, params);
		if (orders == null || orders.isEmpty()) {
			return new ResponseEntity<>(orders, HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<>(orders, HttpStatus.OK);
		}
	}
	
	@GetMapping("/{orderId}/user/{userId}")
	public OrderDataDto readOrderDataByUserId(@PathVariable long userId,
			@PathVariable long orderId) {
		return orderService.readOrderDataByUserId(userId, orderId);
	}
	
	@PostMapping("/user/{userId}")
	public OrderDto create(@PathVariable long userId, @RequestBody OrderDto orderDto) {
		return orderService.create(userId, orderDto);

	}
}
