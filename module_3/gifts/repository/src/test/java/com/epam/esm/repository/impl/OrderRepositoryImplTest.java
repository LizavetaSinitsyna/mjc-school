package com.epam.esm.repository.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.epam.esm.repository.OrderRepository;
import com.epam.esm.repository.model.CertificateModel;
import com.epam.esm.repository.model.OrderCertificateId;
import com.epam.esm.repository.model.OrderCertificateModel;
import com.epam.esm.repository.model.OrderModel;
import com.epam.esm.repository.model.RoleModel;
import com.epam.esm.repository.model.TagModel;
import com.epam.esm.repository.model.UserModel;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@EntityScan("com.epam.esm")
@ComponentScan("com.epam.esm")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class OrderRepositoryImplTest {
	private static final int OFFSET_0 = 0;
	private static final int OFFSET_1 = 1;
	private static final int LIMIT_2 = 2;
	private static final Long ORDER_ID_1 = 1L;
	private static final Long USER_ID_1 = 1L;

	private TagModel tagModel1;
	private TagModel tagModel2;
	private RoleModel roleModel1;
	private UserModel userModel1;
	private UserModel userModel2;
	private OrderModel orderModel1;
	private OrderModel orderModel2;
	private CertificateModel certificateModel1;
	private CertificateModel certificateModel2;
	private BigDecimal cost1;
	private BigDecimal cost2;

	@Autowired
	private OrderRepository orderRepository;

	@PersistenceContext
	private EntityManager entityManager;

	@BeforeEach
	public void setUp() {
		cost1 = new BigDecimal("20");
		cost2 = new BigDecimal("40");

		roleModel1 = new RoleModel();
		roleModel1.setName("user");
		entityManager.persist(roleModel1);

		userModel1 = new UserModel();
		userModel1.setLogin("user1");
		userModel1.setRole(roleModel1);
		entityManager.persist(userModel1);

		userModel2 = new UserModel();
		userModel2.setLogin("user2");
		userModel2.setRole(roleModel1);
		entityManager.persist(userModel2);

		orderModel1 = new OrderModel();
		orderModel1.setUser(userModel1);
		orderModel1.setCost(cost1);

		orderModel2 = new OrderModel();
		orderModel2.setUser(userModel2);
		orderModel2.setCost(cost2);

		tagModel1 = new TagModel();
		tagModel1.setName("food");
		entityManager.persist(tagModel1);

		tagModel2 = new TagModel();
		tagModel2.setName("family");
		entityManager.persist(tagModel2);

		List<TagModel> tagModelList1 = new ArrayList<>();
		tagModelList1.add(tagModel1);
		tagModelList1.add(tagModel2);

		List<TagModel> tagModelList2 = new ArrayList<>();
		tagModelList2.add(tagModel1);

		certificateModel1 = new CertificateModel();
		certificateModel1.setName("Dinner at the restaurant with unlimited pizzas");
		certificateModel1.setDescription("Great present for those who loves pizza");
		certificateModel1.setPrice(new BigDecimal("50.00"));
		certificateModel1.setDuration(30);
		certificateModel1.setTags(tagModelList1);
		entityManager.persist(certificateModel1);

		certificateModel2 = new CertificateModel();
		certificateModel2.setName("Certificate for Museum of Arts");
		certificateModel2.setDescription("Interesting journey into history of art");
		certificateModel2.setPrice(new BigDecimal("45.00"));
		certificateModel2.setDuration(90);
		certificateModel2.setTags(tagModelList2);
		entityManager.persist(certificateModel2);

		OrderCertificateId orderCertificateId1 = new OrderCertificateId();
		OrderCertificateModel orderCertificateModel1 = new OrderCertificateModel();
		orderCertificateModel1.setOrderCertificateId(orderCertificateId1);
		orderCertificateModel1.setCertificate(certificateModel1);
		orderCertificateModel1.setCertificateAmount(1);
		orderCertificateModel1.setOrder(orderModel1);

		List<OrderCertificateModel> orderCertificateModelList1 = new ArrayList<>();
		orderCertificateModelList1.add(orderCertificateModel1);
		orderModel1.setCertificates(orderCertificateModelList1);

		OrderCertificateId orderCertificateId2 = new OrderCertificateId();
		OrderCertificateModel orderCertificateModel2 = new OrderCertificateModel();
		orderCertificateModel2.setOrderCertificateId(orderCertificateId2);
		orderCertificateModel2.setCertificate(certificateModel2);
		orderCertificateModel2.setCertificateAmount(2);
		orderCertificateModel2.setOrder(orderModel2);

		List<OrderCertificateModel> orderCertificateModelList2 = new ArrayList<>();
		orderCertificateModelList2.add(orderCertificateModel2);
		orderModel2.setCertificates(orderCertificateModelList2);
	}

	@Test
	void testSave() {
		OrderModel actual = orderRepository.save(orderModel1);
		Assertions.assertEquals(orderModel1, actual);
	}

	@Test
	void testSaveOrders() {
		List<OrderModel> expected = new ArrayList<>();
		expected.add(orderModel1);
		expected.add(orderModel2);
		List<OrderModel> actual = orderRepository.saveOrders(expected);
		Assertions.assertEquals(expected, actual);

	}

	@Test
	void testFindById() {
		entityManager.persist(orderModel1);
		entityManager.persist(orderModel2);
		Optional<OrderModel> actual = orderRepository.findById(ORDER_ID_1);
		Assertions.assertEquals(Optional.of(orderModel1), actual);
	}

	@Test
	void testFindAll() {
		entityManager.persist(orderModel1);
		entityManager.persist(orderModel2);
		List<OrderModel> actual = orderRepository.findAll(OFFSET_1, LIMIT_2);
		List<OrderModel> expected = Arrays.asList(orderModel2);
		Assertions.assertEquals(expected, actual);
	}

	@Test
	void testReadAllByUserId() {
		entityManager.persist(orderModel1);
		entityManager.persist(orderModel2);
		
		List<OrderModel> expected = new ArrayList<>();
		expected.add(orderModel1);
		List<OrderModel> actual = orderRepository.readAllByUserId(USER_ID_1, OFFSET_0, LIMIT_2);
		Assertions.assertEquals(expected, actual);
	}
}
