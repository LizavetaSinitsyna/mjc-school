package com.epam.esm.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;

import com.epam.esm.dto.CertificateDto;
import com.epam.esm.dto.OrderCertificateDto;
import com.epam.esm.dto.OrderDataDto;
import com.epam.esm.dto.OrderDto;
import com.epam.esm.dto.TagDto;
import com.epam.esm.dto.UserDto;

/**
 * Contains methods for adding HATEOAS links to the entities in response
 *
 */
public class HateoasUtil {
	private HateoasUtil() {

	}

	/**
	 * Adds links to the passed certificate as well as to the tags of this
	 * certificate. If passed certificate is {@code null} does nothing.
	 * 
	 * @param certificateDto the certificate to which links should be added
	 * @see HateoasUtil#addLinksToTag(TagDto)
	 */
	public static void addLinksToCertificate(CertificateDto certificateDto) {
		if (certificateDto != null) {
			certificateDto.add(linkTo(CertificateController.class).slash(certificateDto.getId()).withSelfRel());
			List<TagDto> tags = certificateDto.getTags();
			if (tags != null && !tags.isEmpty()) {
				tags.forEach(tagDto -> addLinksToTag(tagDto));
			}
		}
	}

	/**
	 * Adds links to the passed order as well as to the certificates and user of
	 * this order. If passed order is {@code null} does nothing.
	 * 
	 * @param orderDto the order to which links should be added
	 * @see HateoasUtil#addLinksToCertificate(CertificateDto)
	 * @see HateoasUtil#addLinksToUser(UserDto)
	 */
	public static void addLinksToOrder(OrderDto orderDto) {
		if (orderDto != null) {
			orderDto.add(linkTo(OrderController.class).slash(orderDto.getId()).withSelfRel());
			UserDto userDto = orderDto.getUser();
			userDto.add(linkTo(UserController.class).slash(userDto.getId()).withSelfRel());
			List<OrderCertificateDto> orderCertificateDtos = orderDto.getCertificates();
			if (orderCertificateDtos != null && !orderCertificateDtos.isEmpty()) {
				orderCertificateDtos
						.forEach(orderCertificateDto -> addLinksToCertificate(orderCertificateDto.getCertificate()));
			}
		}
	}

	/**
	 * Adds link to the order with passed id. If passed order data entity is
	 * {@code null} does nothing.
	 * 
	 * @param orderId      the id of the order to which the link should be added
	 * @param orderDataDto the order data entity to which the link should be added
	 */

	public static void addLinksToOrderData(long orderId, OrderDataDto orderDataDto) {
		if (orderDataDto != null) {
			orderDataDto.add(linkTo(OrderController.class).slash(orderId).withSelfRel());
		}
	}

	/**
	 * Adds links to the passed user. If passed user is {@code null} does nothing.
	 * 
	 * @param userDto the user to which links should be added
	 */
	public static void addLinksToUser(UserDto userDto) {
		if (userDto != null) {
			userDto.add(linkTo(UserController.class).slash(userDto.getId()).withSelfRel());
		}
	}

	/**
	 * Adds links to the passed tag. If passed tag is {@code null} does nothing.
	 * 
	 * @param tagDto the tag to which links should be added
	 */
	public static void addLinksToTag(TagDto tagDto) {
		if (tagDto != null) {
			tagDto.add(linkTo(methodOn(TagController.class).readById(tagDto.getId())).withSelfRel());
		}
	}
}
