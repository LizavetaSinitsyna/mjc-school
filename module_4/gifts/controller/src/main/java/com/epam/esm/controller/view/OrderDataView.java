package com.epam.esm.controller.view;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.hateoas.RepresentationModel;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class OrderDataView extends RepresentationModel<OrderDataView> {
	private BigDecimal cost;
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss[.SSS]")
	private LocalDateTime date;
}
