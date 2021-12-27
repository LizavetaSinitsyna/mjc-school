package com.epam.esm.repository.model;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Data;

@Data
@Entity
@Table(name = "orders_certificates")
public class OrderCertificateModel {
	@EmbeddedId
	private OrderCertificateId orderCertificateId;
	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("orderId")
	@JoinColumn(name = "order_id")
	private OrderModel order;
	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("certificateId")
	@JoinColumn(name = "certificate_id")
	private CertificateModel certificate;
	@Column(name = "certificate_amount")
	private Integer certificateAmount;

}
