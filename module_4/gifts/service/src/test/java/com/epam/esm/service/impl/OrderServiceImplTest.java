package com.epam.esm.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.epam.esm.dto.CertificateDto;
import com.epam.esm.dto.OrderCertificateDto;
import com.epam.esm.dto.OrderDataDto;
import com.epam.esm.dto.OrderDto;
import com.epam.esm.dto.TagDto;
import com.epam.esm.dto.UserDto;
import com.epam.esm.exception.NotFoundException;
import com.epam.esm.exception.ValidationException;
import com.epam.esm.repository.CertificateRepository;
import com.epam.esm.repository.OrderRepository;
import com.epam.esm.repository.UserRepository;
import com.epam.esm.repository.model.TagModel;
import com.epam.esm.repository.model.UserModel;
import com.epam.esm.repository.model.CertificateModel;
import com.epam.esm.repository.model.OrderCertificateId;
import com.epam.esm.repository.model.OrderCertificateModel;
import com.epam.esm.repository.model.OrderModel;
import com.epam.esm.repository.model.EntityConstant;
import com.epam.esm.service.OrderService;
import com.epam.esm.service.ServiceConstant;
import com.epam.esm.service.converter.OrderConverter;
import com.epam.esm.service.converter.OrderDataConverter;
import com.epam.esm.service.converter.PageConverter;
import com.epam.esm.service.validation.OrderValidation;

class OrderServiceImplTest {
	private static final int PAGE_NUMBER = 0;
	private static final int LIMIT = 1;
	private static final Long ORDER_ID_1 = 1L;
	private static final Long CERTIFICATE_ID_1 = 1L;
	private static final Long USER_ID_1 = 1L;
	private static final long USER_ID_2 = 2L;

	private OrderRepository orderRepository;
	private UserRepository userRepository;
	private CertificateRepository certificateRepository;
	private static OrderConverter orderConverter;
	private static OrderDataConverter orderDataConverter;
	private static PageConverter<OrderDto, OrderModel> pageConverter;
	private static OrderValidation orderValidation;
	private static OrderService orderService;
	private TagModel tagModel1;
	private TagDto tagDto1;
	private UserModel userModel1;
	private UserDto userDto1;
	private OrderModel orderModel1;
	private OrderDto orderDto1;
	private OrderDataDto orderDataDto1;
	private CertificateModel certificateModel1;
	private CertificateDto certificateDto1;
	private BigDecimal cost;
	private Page<OrderModel> orderModelsPage;
	private Page<OrderDto> orderDtosPage;

	@BeforeAll
	public static void init() {
		orderConverter = new OrderConverter();
		orderDataConverter = new OrderDataConverter();
		orderValidation = new OrderValidation();
		pageConverter = new PageConverter<>();
	}

	@BeforeEach
	public void setUp() {
		certificateRepository = Mockito.mock(CertificateRepository.class);
		orderRepository = Mockito.mock(OrderRepository.class);
		userRepository = Mockito.mock(UserRepository.class);
		orderService = new OrderServiceImpl(orderRepository, userRepository, certificateRepository, orderConverter,
				orderDataConverter, orderValidation, pageConverter);

		cost = new BigDecimal("20");

		userModel1 = new UserModel();
		userModel1.setLogin("user1");

		orderModel1 = new OrderModel();
		orderModel1.setUser(userModel1);
		orderModel1.setCost(cost);

		tagModel1 = new TagModel();
		tagModel1.setName("food");

		List<TagModel> tagModelList1 = new ArrayList<>();
		tagModelList1.add(tagModel1);

		certificateModel1 = new CertificateModel();
		certificateModel1.setId(CERTIFICATE_ID_1);
		certificateModel1.setName("Dinner at the restaurant with unlimited pizzas");
		certificateModel1.setDescription("Great present for those who loves pizza");
		certificateModel1.setPrice(new BigDecimal("50.00"));
		certificateModel1.setDuration(30);
		certificateModel1.setTags(tagModelList1);

		OrderCertificateId orderCertificateId = new OrderCertificateId();
		orderCertificateId.setCertificateId(CERTIFICATE_ID_1);
		orderCertificateId.setOrderId(ORDER_ID_1);
		OrderCertificateModel orderCertificateModel = new OrderCertificateModel();
		orderCertificateModel.setOrderCertificateId(orderCertificateId);
		orderCertificateModel.setCertificate(certificateModel1);
		orderCertificateModel.setCertificateAmount(1);
		orderCertificateModel.setOrder(orderModel1);

		List<OrderCertificateModel> orderCertificateModelList = new ArrayList<>();
		orderCertificateModelList.add(orderCertificateModel);
		orderModel1.setCertificates(orderCertificateModelList);

		userDto1 = new UserDto();
		userDto1.setLogin("user1");

		orderDto1 = new OrderDto();
		orderDto1.setUser(userDto1);
		orderDto1.setCost(new BigDecimal("20"));

		tagDto1 = new TagDto();
		tagDto1.setName("food");

		List<TagDto> tagDtoList1 = new ArrayList<>();
		tagDtoList1.add(tagDto1);

		certificateDto1 = new CertificateDto();
		certificateDto1.setId(CERTIFICATE_ID_1);
		certificateDto1.setName("Dinner at the restaurant with unlimited pizzas");
		certificateDto1.setDescription("Great present for those who loves pizza");
		certificateDto1.setPrice(new BigDecimal("50.00"));
		certificateDto1.setDuration(30);
		certificateDto1.setTags(tagDtoList1);

		OrderCertificateDto orderCertificateDto = new OrderCertificateDto();
		orderCertificateDto.setCertificate(certificateDto1);
		orderCertificateDto.setCertificateAmount(1);

		List<OrderCertificateDto> orderCertificateDtoList = new ArrayList<>();
		orderCertificateDtoList.add(orderCertificateDto);
		orderDto1.setCertificates(orderCertificateDtoList);

		orderDataDto1 = new OrderDataDto();
		orderDataDto1.setCost(cost);

		List<OrderDto> orderDtos = new ArrayList<>();
		orderDtos.add(orderDto1);

		List<OrderModel> orderModels = new ArrayList<>();
		orderModels.add(orderModel1);

		Pageable pageable = PageRequest.of(PAGE_NUMBER, LIMIT);
		orderModelsPage = new PageImpl<>(orderModels, pageable, orderModels.size());
		orderDtosPage = new PageImpl<>(orderDtos, pageable, orderDtos.size());
	}

	@Test
	void testReadById() {
		OrderDto expected = orderDto1;

		Mockito.when(orderRepository.findById(ORDER_ID_1)).thenReturn(Optional.of(orderModel1));

		OrderDto actual = orderService.readById(ORDER_ID_1);
		Assertions.assertEquals(expected, actual);

		Mockito.verify(orderRepository).findById(Mockito.anyLong());
	}

	@Test
	void testReadByIdWithNegativeId() {
		Assertions.assertThrows(ValidationException.class, () -> {
			orderService.readById(-2);
		});
	}

	@Test
	void testReadAllByUserId() {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.put(ServiceConstant.OFFSET, Arrays.asList(Integer.toString(PAGE_NUMBER)));
		params.put(ServiceConstant.LIMIT, Arrays.asList(Integer.toString(LIMIT)));

		Page<OrderDto> expected = orderDtosPage;

		Mockito.when(userRepository.userExistsById(USER_ID_1)).thenReturn(true);
		Mockito.when(orderRepository.readAllByUserId(USER_ID_1, PAGE_NUMBER, LIMIT)).thenReturn(orderModelsPage);

		Page<OrderDto> actual = orderService.readAllByUserId(USER_ID_1, params);
		Assertions.assertEquals(expected, actual);

		Mockito.verify(userRepository).userExistsById(USER_ID_1);
		Mockito.verify(orderRepository).readAllByUserId(USER_ID_1, PAGE_NUMBER, LIMIT);
	}

	@Test
	void testReadAllByUserIdWithNonExistedUserId() {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

		Mockito.when(userRepository.userExistsById(USER_ID_1)).thenReturn(false);

		Assertions.assertThrows(NotFoundException.class, () -> {
			orderService.readAllByUserId(USER_ID_1, params);
		});
	}

	@Test
	void testReadAllByUserIdWithInvalidUserId() {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

		Assertions.assertThrows(ValidationException.class, () -> {
			orderService.readAllByUserId(-100, params);
		});
	}

	@Test
	void testCreate() {
		OrderDto expected = orderDto1;

		Mockito.when(userRepository.findById(USER_ID_1)).thenReturn(Optional.of(userModel1));
		Mockito.when(certificateRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(certificateModel1));
		Mockito.when(certificateRepository.certificateExistsById(Mockito.anyLong())).thenReturn(true);
		Mockito.when(orderRepository.save(Mockito.any())).thenReturn(orderModel1);

		OrderDto actual = orderService.create(USER_ID_1, orderDto1);

		Assertions.assertEquals(expected, actual);

		Mockito.verify(userRepository).findById(USER_ID_1);
		Mockito.verify(certificateRepository).findById(Mockito.anyLong());
		Mockito.verify(certificateRepository).certificateExistsById(Mockito.anyLong());
		Mockito.verify(orderRepository).save(Mockito.any());
	}

	@Test
	void testCreateWithNullCertificatesList() {
		orderDto1.setCertificates(null);

		Mockito.when(userRepository.findById(USER_ID_1)).thenReturn(Optional.of(userModel1));

		Assertions.assertThrows(ValidationException.class, () -> {
			orderService.create(USER_ID_1, orderDto1);
		});
	}

	@Test
	void testCreateOrders() {
		List<OrderDto> expected = new ArrayList<>();
		orderDto1.getUser().setId(USER_ID_1);
		expected.add(orderDto1);
		userModel1.setId(USER_ID_1);

		Mockito.when(userRepository.findById(USER_ID_1)).thenReturn(Optional.of(userModel1));
		Mockito.when(certificateRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(certificateModel1));
		Mockito.when(certificateRepository.certificateExistsById(Mockito.anyLong())).thenReturn(true);
		Mockito.when(orderRepository.saveOrders(Mockito.any())).thenReturn(Arrays.asList(orderModel1));

		List<OrderDto> actual = orderService.createOrders(expected);

		Assertions.assertEquals(expected, actual);

		Mockito.verify(userRepository).findById(USER_ID_1);
		Mockito.verify(certificateRepository).findById(Mockito.anyLong());
		Mockito.verify(certificateRepository).certificateExistsById(Mockito.anyLong());
		Mockito.verify(orderRepository).saveOrders(Mockito.any());
	}

	@Test
	void testCreateWithInvalidCertificateAmount() {
		orderDto1.getCertificates().get(0).setCertificateAmount(-2);

		Mockito.when(userRepository.findById(USER_ID_1)).thenReturn(Optional.of(userModel1));
		Mockito.when(certificateRepository.certificateExistsById(Mockito.anyLong())).thenReturn(true);

		Assertions.assertThrows(ValidationException.class, () -> {
			orderService.create(USER_ID_1, orderDto1);
		});
	}

	@Test
	void testReadOrderDataByUserId() {
		userModel1.setId(USER_ID_1);
		OrderDataDto expected = orderDataDto1;
		Mockito.when(orderRepository.findById(ORDER_ID_1)).thenReturn(Optional.of(orderModel1));

		OrderDataDto actual = orderService.readOrderDataByUserId(USER_ID_1, ORDER_ID_1);
		Assertions.assertEquals(expected, actual);

		Mockito.verify(orderRepository).findById(ORDER_ID_1);
	}

	@Test
	void testReadOrderDataByUserIdWithInvalidId() {
		userModel1.setId(USER_ID_1);
		Mockito.when(orderRepository.findById(ORDER_ID_1)).thenReturn(Optional.of(orderModel1));

		Assertions.assertThrows(ValidationException.class, () -> {
			orderService.readOrderDataByUserId(USER_ID_2, ORDER_ID_1);
		});
	}

	@Test
	void testReadAll() {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.put(ServiceConstant.OFFSET, Arrays.asList(Integer.toString(PAGE_NUMBER)));
		params.put(ServiceConstant.LIMIT, Arrays.asList(Integer.toString(LIMIT)));

		Page<OrderDto> expected = orderDtosPage;

		Mockito.when(orderRepository.findAll(PAGE_NUMBER, LIMIT)).thenReturn(orderModelsPage);

		Page<OrderDto> actual = orderService.readAll(params);
		Assertions.assertEquals(expected, actual);

		Mockito.verify(orderRepository).findAll(PAGE_NUMBER, LIMIT);
	}

	@Test
	void testReadAllWithInvalidReadParam() {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.put(EntityConstant.SEARCH, Arrays.asList("dinner"));

		Assertions.assertThrows(ValidationException.class, () -> {
			orderService.readAll(params);
		});
	}
}
