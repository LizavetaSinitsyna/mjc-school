package com.epam.esm.controller.view;

import lombok.Data;

@Data
public class OrderCertificateView {
	private CertificateView certificate;
	private Integer certificateAmount;
}
