package com.epam.esm.repository.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.epam.esm.repository.audit.TagAuditListener;

import lombok.Data;

@Data
@Entity
@Table(name = "tags")
@EntityListeners(TagAuditListener.class)
public class TagModel {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, updatable = false)
	private Long id;
	@Column(nullable = false, length = 25, unique = true)
	private String name;
	@Column(name = "is_deleted", nullable = false)
	private boolean isDeleted;
	@ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
	List<CertificateModel> certificates;
}
