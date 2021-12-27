package com.epam.esm.repository.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Data;

@Data
@Embeddable
public class OrderCertificateId implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Column(name = "order_id")
	private Long orderId;
	@Column(name = "certificate_id")
	private Long certificateId;
	

}
