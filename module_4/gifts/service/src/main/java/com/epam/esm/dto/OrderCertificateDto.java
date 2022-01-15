package com.epam.esm.dto;

import lombok.Data;

@Data
public class OrderCertificateDto {
	private CertificateDto certificate;
	private Integer certificateAmount;
}
