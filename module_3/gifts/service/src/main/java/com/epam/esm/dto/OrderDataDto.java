package com.epam.esm.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.hateoas.RepresentationModel;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class OrderDataDto extends RepresentationModel<OrderDataDto> {
	private BigDecimal cost;
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss[.SSS]")
	private LocalDateTime date;
}
