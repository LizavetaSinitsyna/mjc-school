package com.epam.esm.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

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

import com.epam.esm.dto.CertificateDto;
import com.epam.esm.dto.OrderCertificateDto;
import com.epam.esm.dto.OrderDataDto;
import com.epam.esm.dto.OrderDto;
import com.epam.esm.dto.UserDto;
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
		OrderDto orderDto = orderService.readById(id);
		addLinksToOrder(orderDto);
		return orderDto;
	}

	/**
	 * Reads all orders according to passed parameters.
	 * 
	 * @param params the parameters which define the choice of orders and their
	 *               ordering
	 * @return orders which meet passed parameters
	 */
	@GetMapping
	public ResponseEntity<List<OrderDto>> readAll(@RequestParam MultiValueMap<String, String> params) {
		List<OrderDto> orders = orderService.readAll(params);
		if (orders == null || orders.isEmpty()) {
			return new ResponseEntity<>(orders, HttpStatus.NO_CONTENT);
		} else {
			for (OrderDto orderDto : orders) {
				addLinksToOrder(orderDto);
			}
			return new ResponseEntity<>(orders, HttpStatus.OK);
		}
	}

	/**
	 * Reads all orders for specified user according to passed parameters.
	 * 
	 * @param userId id of the user whose orders should be read
	 * @param params the parameters which define the choice of orders and their
	 *               ordering
	 * @return orders for specified user which meet passed parameters
	 */
	@GetMapping("/user/{userId}")
	public ResponseEntity<List<OrderDto>> readByUserId(@PathVariable long userId,
			@RequestParam MultiValueMap<String, String> params) {
		List<OrderDto> orders = orderService.readAllByUserId(userId, params);
		if (orders == null || orders.isEmpty()) {
			return new ResponseEntity<>(orders, HttpStatus.NO_CONTENT);
		} else {
			for (OrderDto orderDto : orders) {
				addLinksToOrder(orderDto);
			}
			return new ResponseEntity<>(orders, HttpStatus.OK);
		}
	}

	/**
	 * Reads information about the order with passed id for specified user.
	 * 
	 * @param userId  id of the user whose order should be read
	 * @param orderId id of the order to be read
	 * @return information about the order with passed id for specified user
	 */
	@GetMapping("/{orderId}/user/{userId}")
	public OrderDataDto readOrderDataByUserId(@PathVariable long userId, @PathVariable long orderId) {
		OrderDataDto orderDataDto = orderService.readOrderDataByUserId(userId, orderId);
		orderDataDto.add(linkTo(OrderController.class).slash(orderId).withSelfRel());
		return orderService.readOrderDataByUserId(userId, orderId);
	}

	/**
	 * Creates and saves the passed order.
	 * 
	 * @param userId   id of the user whose order should be saved
	 * @param orderDto the order to be saved
	 * @return saved order
	 */
	@PostMapping("/user/{userId}")
	public OrderDto create(@PathVariable long userId, @RequestBody OrderDto orderDto) {
		OrderDto createdOrderDto = orderService.create(userId, orderDto);
		addLinksToOrder(createdOrderDto);
		return createdOrderDto;

	}

	private void addLinksToOrder(OrderDto orderDto) {
		if (orderDto != null) {
			orderDto.add(linkTo(OrderController.class).slash(orderDto.getId()).withSelfRel());
			UserDto userDto = orderDto.getUser();
			userDto.add(linkTo(UserController.class).slash(userDto.getId()).withSelfRel());
			List<OrderCertificateDto> orderCertificateDtos = orderDto.getCertificates();
			if (orderCertificateDtos != null && !orderCertificateDtos.isEmpty()) {
				for (OrderCertificateDto orderCertificateDto : orderCertificateDtos) {
					CertificateDto certificateDto = orderCertificateDto.getCertificate();
					CertificateController.addLinksToCertificate(certificateDto);
				}
			}
		}

	}

}
