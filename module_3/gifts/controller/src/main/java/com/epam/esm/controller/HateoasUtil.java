package com.epam.esm.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import com.epam.esm.controller.view.CertificateView;
import com.epam.esm.controller.view.OrderCertificateView;
import com.epam.esm.controller.view.OrderDataView;
import com.epam.esm.controller.view.OrderView;
import com.epam.esm.controller.view.PageView;
import com.epam.esm.controller.view.TagView;
import com.epam.esm.controller.view.UserView;
import com.epam.esm.service.ServiceConstant;

/**
 * Contains methods for adding HATEOAS links to the entities in response
 *
 */
public class HateoasUtil {
	private static final String NEXT_PAGE = "nextPage";
	private static final String PREVIOUS_PAGE = "prevPage";

	private HateoasUtil() {

	}

	/**
	 * Adds links to the passed certificate as well as to the tags of this
	 * certificate. If passed certificate is {@code null} does nothing.
	 * 
	 * @param certificateView the certificate to which links should be added
	 * @see HateoasUtil#addLinksToTag(TagDto)
	 */
	public static void addLinksToCertificate(CertificateView certificateView) {
		if (certificateView != null) {
			certificateView.add(linkTo(CertificateController.class).slash(certificateView.getId()).withSelfRel());
			List<TagView> tags = certificateView.getTags();
			if (tags != null && !tags.isEmpty()) {
				tags.forEach(tagDto -> addLinksToTag(tagDto));
			}
		}
	}

	/**
	 * Adds links to the passed order as well as to the certificates and user of
	 * this order. If passed order is {@code null} does nothing.
	 * 
	 * @param orderView the order to which links should be added
	 * @see HateoasUtil#addLinksToCertificate(CertificateDto)
	 * @see HateoasUtil#addLinksToUser(UserDto)
	 */
	public static void addLinksToOrder(OrderView orderView) {
		if (orderView != null) {
			orderView.add(linkTo(OrderController.class).slash(orderView.getId()).withSelfRel());
			UserView userDto = orderView.getUser();
			userDto.add(linkTo(UserController.class).slash(userDto.getId()).withSelfRel());
			List<OrderCertificateView> orderCertificateDtos = orderView.getCertificates();
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
	 * @param orderDataView the order data entity to which the link should be added
	 */

	public static void addLinksToOrderData(long orderId, OrderDataView orderDataView) {
		if (orderDataView != null) {
			orderDataView.add(linkTo(OrderController.class).slash(orderId).withSelfRel());
		}
	}

	/**
	 * Adds links to the passed user. If passed user is {@code null} does nothing.
	 * 
	 * @param userView the user to which links should be added
	 */
	public static void addLinksToUser(UserView userView) {
		if (userView != null) {
			userView.add(linkTo(UserController.class).slash(userView.getId()).withSelfRel());
		}
	}

	/**
	 * Adds links to the passed tag. If passed tag is {@code null} does nothing.
	 * 
	 * @param tagView the tag to which links should be added
	 */
	public static void addLinksToTag(TagView tagView) {
		if (tagView != null) {
			tagView.add(linkTo(methodOn(TagController.class).readById(tagView.getId())).withSelfRel());
		}
	}

	public static void addLinksToPage(PageView<?> pageView, WebMvcLinkBuilder controllerMethodLink) {
		if (pageView != null && controllerMethodLink != null) {
			pageView.add(controllerMethodLink.withSelfRel());
			UriComponentsBuilder builder = controllerMethodLink.toUriComponentsBuilder();
			builder.replaceQueryParam(ServiceConstant.OFFSET, pageView.getCurrentPage() + 1);
			Link nextPageLink = Link.of(builder.build().toString(), NEXT_PAGE);
			pageView.add(nextPageLink);
			if (pageView.getCurrentPage() > ServiceConstant.MIN_PAGE_NUMBER) {
				builder.replaceQueryParam(ServiceConstant.OFFSET, pageView.getCurrentPage() - 1);
				Link prevPageLink = Link.of(builder.build().toString(), PREVIOUS_PAGE);
				pageView.add(prevPageLink);
			}
		}
	}
}
