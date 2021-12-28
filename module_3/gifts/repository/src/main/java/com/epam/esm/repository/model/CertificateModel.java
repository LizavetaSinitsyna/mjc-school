package com.epam.esm.repository.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.epam.esm.repository.audit.CertificateAuditListener;

import lombok.Data;

@Data
@Entity
@Table(name = "gift_certificates")
@EntityListeners(CertificateAuditListener.class)
public class CertificateModel {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, updatable = false)
	private Long id;
	@Column(nullable = false, length = 50)
	private String name;
	@Column(nullable = false, length = 1000)
	private String description;
	@Column(nullable = false, precision = 7, scale = 2)
	private BigDecimal price;
	@Column(nullable = false)
	private Integer duration;
	@Column(name = "create_date", nullable = false, updatable = false)
	private LocalDateTime createDate;
	@Column(name = "last_update_date", nullable = false)
	private LocalDateTime lastUpdateDate;
	@Column(name = "is_deleted", nullable = false)
	private boolean isDeleted;
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "tags_certificates", joinColumns = @JoinColumn(name = "certificate_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "tag_id", referencedColumnName = "id"))
	List<TagModel> tags;
}
