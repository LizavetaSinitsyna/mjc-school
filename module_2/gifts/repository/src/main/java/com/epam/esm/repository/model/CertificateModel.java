package com.epam.esm.repository.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class CertificateModel {
	private long id;
	private String name;
	private boolean isDeleted;
	private String description;
	private BigDecimal price;
	private int duration;
	private LocalDateTime lastUpdateDate;
	private LocalDateTime createDate;
}
