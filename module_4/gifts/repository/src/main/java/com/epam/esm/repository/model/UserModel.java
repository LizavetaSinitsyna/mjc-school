package com.epam.esm.repository.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "users")
public class UserModel {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, updatable = false)
	private Long id;
	@Column(nullable = false, length = 25, unique = true)
	private String login;
	@ManyToOne
	@JoinColumn(name = "role_id", nullable = false)
	private RoleModel role;
	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private List<OrderModel> orders;
}
