package com.epam.esm.repository.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.epam.esm.repository.audit.OrderAuditListener;

import lombok.Data;

@Data
@Entity
@Table(name = "orders")
@EntityListeners(OrderAuditListener.class)
public class OrderModel {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, updatable = false)
	private Long id;
	@Column(nullable = false, scale = 2)
	private BigDecimal cost;
	@Column(nullable = false, updatable = false)
	private LocalDateTime date;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false, updatable = false)
	private UserModel user;
	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	List<OrderCertificateModel> certificates;
}
