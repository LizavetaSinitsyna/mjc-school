package com.epam.esm.repository.impl;

import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.annotation.DirtiesContext;

import com.epam.esm.repository.RoleRepository;
import com.epam.esm.repository.model.RoleModel;

@DataJpaTest
@EntityScan("com.epam.esm")
@ComponentScan("com.epam.esm")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class RoleRepositoryImplTest {
	private RoleModel roleModel1;

	@Autowired
	private RoleRepository roleRepository;

	@PersistenceContext
	private EntityManager entityManager;

	@BeforeEach
	public void setUp() {
		roleModel1 = new RoleModel();
		roleModel1.setName("user");
	}

	@Test
	void testFindByName() {
		entityManager.persist(roleModel1);
		Optional<RoleModel> actual = roleRepository.findByName("user");
		Assertions.assertEquals(Optional.of(roleModel1), actual);
	}
}
