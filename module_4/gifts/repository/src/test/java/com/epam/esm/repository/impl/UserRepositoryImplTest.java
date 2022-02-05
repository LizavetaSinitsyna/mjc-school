package com.epam.esm.repository.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;

import com.epam.esm.repository.UserRepository;
import com.epam.esm.repository.model.RoleModel;
import com.epam.esm.repository.model.UserModel;

@DataJpaTest
@EntityScan("com.epam.esm")
@ComponentScan("com.epam.esm")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserRepositoryImplTest {
	private static final String PASSWORD = "Password1!";
	private static final int OFFSET_1 = 1;
	private static final int LIMIT_1 = 1;
	private static final Long USER_ID_1 = 1L;

	private RoleModel roleModel1;
	private UserModel userModel1;
	private UserModel userModel2;

	@Autowired
	private UserRepository userRepository;

	@PersistenceContext
	private EntityManager entityManager;

	@BeforeEach
	public void setUp() {
		roleModel1 = new RoleModel();
		roleModel1.setName("user");
		entityManager.persist(roleModel1);

		userModel1 = new UserModel();
		userModel1.setUsername("user1");
		userModel1.setRole(roleModel1);
		userModel1.setPassword(PASSWORD);
		entityManager.persist(userModel1);

		userModel2 = new UserModel();
		userModel2.setUsername("user2");
		userModel2.setRole(roleModel1);
		userModel2.setPassword(PASSWORD);
	}

	@Test
	void testSave() {
		UserModel actual = userRepository.save(userModel1);
		Assertions.assertEquals(userModel1, actual);
	}

	@Test
	void testSaveUsers() {
		List<UserModel> expected = new ArrayList<>();
		expected.add(userModel1);
		expected.add(userModel2);
		List<UserModel> actual = userRepository.saveUsers(expected);
		Assertions.assertEquals(expected, actual);
	}

	@Test
	void testFindById() {
		entityManager.persist(userModel1);
		entityManager.persist(userModel2);
		Optional<UserModel> actual = userRepository.findById(USER_ID_1);
		Assertions.assertEquals(Optional.of(userModel1), actual);
	}

	@Test
	void testUserExistsById() {
		entityManager.persist(userModel1);
		boolean actual = userRepository.userExistsById(USER_ID_1);
		Assertions.assertTrue(actual);
	}

	@Test
	void testFindByLogin() {
		entityManager.persist(userModel1);
		entityManager.persist(userModel2);
		Optional<UserModel> actual = userRepository.findByLogin("user2");
		Assertions.assertEquals(Optional.of(userModel2), actual);
	}

	@Test
	void testUserExistsByLogin() {
		entityManager.persist(userModel1);
		entityManager.persist(userModel2);
		boolean actual = userRepository.userExistsByLogin("user1");
		Assertions.assertTrue(actual);
	}

	@Test
	void testFindAll() {
		entityManager.persist(userModel1);
		entityManager.persist(userModel2);

		Pageable pageable = PageRequest.of(OFFSET_1, LIMIT_1);
		List<UserModel> expectedList = Arrays.asList(userModel2);
		Page<UserModel> expected = new PageImpl<>(expectedList, pageable, expectedList.size());

		Page<UserModel> actual = userRepository.findAll(OFFSET_1, LIMIT_1);

		Assertions.assertEquals(expected, actual);
	}
}
