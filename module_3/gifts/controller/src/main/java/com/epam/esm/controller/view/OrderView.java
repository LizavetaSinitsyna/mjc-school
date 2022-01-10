package com.epam.esm.controller.view;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.hateoas.RepresentationModel;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class OrderView extends RepresentationModel<OrderView> {
	private Long id;
	private BigDecimal cost;
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss[.SSS]")
	private LocalDateTime date;
	private UserView user;
	private List<OrderCertificateView> certificates;
}
