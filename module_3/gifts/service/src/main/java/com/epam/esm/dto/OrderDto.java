package com.epam.esm.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.hateoas.RepresentationModel;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class OrderDto extends RepresentationModel<OrderDto> {
	private Long id;
	private BigDecimal cost;
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss[.SSS]")
	private LocalDateTime date;
	private UserDto user;
	private List<OrderCertificateDto> certificates;
}
