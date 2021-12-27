package com.epam.esm.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import com.epam.esm.dto.OrderCertificateDto;
import com.epam.esm.dto.OrderDataDto;
import com.epam.esm.dto.OrderDto;
import com.epam.esm.dto.UserDto;
import com.epam.esm.exception.ErrorCode;
import com.epam.esm.exception.NotFoundException;
import com.epam.esm.exception.ValidationException;
import com.epam.esm.repository.CertificateRepository;
import com.epam.esm.repository.OrderRepository;
import com.epam.esm.repository.UserRepository;
import com.epam.esm.repository.model.CertificateModel;
import com.epam.esm.repository.model.EntityConstant;
import com.epam.esm.repository.model.OrderCertificateModel;
import com.epam.esm.repository.model.OrderModel;
import com.epam.esm.service.DateTimeWrapper;
import com.epam.esm.service.OrderService;
import com.epam.esm.service.converter.OrderCertificateConverter;
import com.epam.esm.service.converter.OrderConverter;
import com.epam.esm.service.converter.OrderDataConverter;
import com.epam.esm.service.validation.OrderValidation;
import com.epam.esm.service.validation.Util;

/**
 * 
 * Contains methods implementation for working mostly with {@code UserDto}
 * entity.
 *
 */
@Service
public class OrderServiceImpl implements OrderService {
	private static final int OFFSET = 0;
	private static final int LIMIT = 10;

	private OrderRepository orderRepository;
	private UserRepository userRepository;
	private CertificateRepository certificateRepository;
	private OrderConverter orderConverter;
	private OrderDataConverter orderDataConverter;
	private OrderCertificateConverter orderCertificateConverter;
	private OrderValidation orderValidation;
	private DateTimeWrapper dateTimeWrapper;

	@Autowired
	public OrderServiceImpl(OrderRepository orderRepository, UserRepository userRepository,
			CertificateRepository certificateRepository, OrderConverter orderConverter,
			OrderDataConverter orderDataConverter, OrderCertificateConverter orderCertificateConverter,
			OrderValidation orderValidation, DateTimeWrapper dateTimeWrapper) {
		super();
		this.orderRepository = orderRepository;
		this.userRepository = userRepository;
		this.certificateRepository = certificateRepository;
		this.orderConverter = orderConverter;
		this.orderDataConverter = orderDataConverter;
		this.orderCertificateConverter = orderCertificateConverter;
		this.orderValidation = orderValidation;
		this.dateTimeWrapper = dateTimeWrapper;
	}

	/**
	 * Reads user with passed id.
	 * 
	 * @param userId id of user to be read
	 * @return user with passed id
	 */
	@Override
	public OrderDto readById(long orderId) {
		if (!Util.isPositive(orderId)) {
			throw new ValidationException(EntityConstant.ID + Util.ERROR_RESOURCE_DELIMITER + orderId,
					ErrorCode.INVALID_ORDER_ID);
		}

		Optional<OrderModel> orderModel = orderRepository.findById(orderId);

		if (orderModel.isEmpty()) {
			throw new NotFoundException(EntityConstant.ID + Util.ERROR_RESOURCE_DELIMITER + orderId,
					ErrorCode.NO_ORDER_FOUND);
		}

		OrderDto orderDto = orderConverter.convertToDto(orderModel.get());

		return orderDto;
	}

	/**
	 * Reads all users according to passed parameters.
	 * 
	 * @param params the parameters which define choice of users and their ordering
	 * @return users which meet passed parameters
	 */
	@Override
	public List<OrderDto> readAllByUserId(long userId, MultiValueMap<String, String> params) {
		MultiValueMap<String, String> paramsInLowerCase = Util.mapToLowerCase(params);
		if (!Util.isPositive(userId)) {
			throw new ValidationException(EntityConstant.ID + Util.ERROR_RESOURCE_DELIMITER + userId,
					ErrorCode.INVALID_USER_ID);
		}
		if (!userRepository.userExistsById(userId)) {
			throw new NotFoundException(EntityConstant.ID + Util.ERROR_RESOURCE_DELIMITER + userId,
					ErrorCode.NO_USER_FOUND);
		}
		Map<ErrorCode, String> errors = orderValidation.validateReadParams(paramsInLowerCase);

		if (!errors.isEmpty()) {
			throw new ValidationException(errors, ErrorCode.INVALID_ORDER_REQUEST_PARAMS);
		}

		int offset = OFFSET;
		int limit = LIMIT;

		if (params.containsKey(EntityConstant.OFFSET)) {
			offset = Integer.parseInt(params.get(EntityConstant.OFFSET).get(0));
		}

		if (params.containsKey(EntityConstant.LIMIT)) {
			limit = Integer.parseInt(params.get(EntityConstant.LIMIT).get(0));
		}

		List<OrderModel> orderModels = orderRepository.readAllByUserId(userId, offset, limit);
		List<OrderDto> orderDtos = new ArrayList<>(orderModels.size());
		for (OrderModel orderModel : orderModels) {
			orderDtos.add(orderConverter.convertToDto(orderModel));
		}
		return orderDtos;
	}

	@Override
	public OrderDto create(long userId, OrderDto orderDto) {
		Util.checkNull(orderDto);
		if (!userRepository.userExistsById(userId)) {
			throw new NotFoundException(EntityConstant.ID + Util.ERROR_RESOURCE_DELIMITER + userId,
					ErrorCode.NO_USER_FOUND);
		}
		List<OrderCertificateDto> orderCertificates = orderDto.getCertificates();
		if (orderCertificates == null || orderCertificates.isEmpty()) {
			throw new NotFoundException(
					EntityConstant.ORDER_CERTIFICATES + Util.ERROR_RESOURCE_DELIMITER + orderCertificates,
					ErrorCode.NO_ORDER_CERTIFICATES_FOUND);
		}

		BigDecimal cost = new BigDecimal("0");
		//List<OrderCertificateModel> managedOrderCertificateModels = new ArrayList<>();
		for (OrderCertificateDto orderCertificate : orderCertificates) {
			Long certificateId = orderCertificate.getCertificate().getId();
			if (!Util.isPositive(certificateId)) {
				throw new ValidationException(EntityConstant.ID + Util.ERROR_RESOURCE_DELIMITER + certificateId,
						ErrorCode.INVALID_CERTIFICATE_ID);
			}
			Optional<CertificateModel> certificateModel = certificateRepository.findById(certificateId);
			if (certificateModel.isEmpty()) {
				throw new NotFoundException(EntityConstant.ID + Util.ERROR_RESOURCE_DELIMITER + certificateId,
						ErrorCode.NO_CERTIFICATE_FOUND);
			}
			int certificateAmount = orderCertificate.getCertificateAmount();
			if (certificateAmount < 1) {
				throw new ValidationException(EntityConstant.ID + Util.ERROR_RESOURCE_DELIMITER + certificateAmount,
						ErrorCode.NEGATIVE_ORDER_CERTIFICATE_AMOUNT);
			}

			cost = cost.add(certificateModel.get().getPrice().multiply(new BigDecimal(certificateAmount)));
			/*-OrderCertificateModel orderCertificateModel = orderCertificateConverter.convertToModel(orderCertificate);
			orderCertificateModel.setCertificate(certificateModel.get());
			managedOrderCertificateModels.add(orderCertificateModel);*/
		}
		LocalDateTime now = dateTimeWrapper.obtainCurrentDateTime();

		UserDto user = new UserDto();
		user.setId(userId);

		orderDto.setDate(now);
		orderDto.setCost(cost);
		orderDto.setUser(user);

		OrderModel orderToSave = orderConverter.convertToModel(orderDto);
		/*-orderToSave.setCertificates(managedOrderCertificateModels);
		for (OrderCertificateModel certificate : orderToSave.getCertificates()) {
			certificate.setOrder(orderToSave);
		}*/

		OrderModel savedOrder = orderRepository.save(orderToSave);

		return orderConverter.convertToDto(savedOrder);
	}

	@Override
	public OrderDataDto readOrderDataByUserId(long userId, long orderId) {
		if (!Util.isPositive(orderId)) {
			throw new ValidationException(EntityConstant.ID + Util.ERROR_RESOURCE_DELIMITER + orderId,
					ErrorCode.INVALID_ORDER_ID);
		}

		Optional<OrderModel> orderModel = orderRepository.findById(orderId);

		if (orderModel.isEmpty()) {
			throw new NotFoundException(EntityConstant.ID + Util.ERROR_RESOURCE_DELIMITER + orderId,
					ErrorCode.NO_ORDER_FOUND);
		} else if (orderModel.get().getUser().getId() != userId) {
			StringBuilder errorMessage = new StringBuilder();
			errorMessage.append(EntityConstant.USER_ID + Util.ERROR_RESOURCE_DELIMITER + userId);
			errorMessage.append(Util.ERROR_RESOURCES_LIST_DELIMITER);
			errorMessage.append(EntityConstant.ORDER_ID + Util.ERROR_RESOURCE_DELIMITER + orderId);
			throw new ValidationException(errorMessage.toString(), ErrorCode.USER_ID_MISMATCH);
		}

		OrderDataDto orderDataDto = orderDataConverter.convertToDto(orderModel.get());

		return orderDataDto;
	}

	@Override
	public List<OrderDto> readAll(MultiValueMap<String, String> params) {
		MultiValueMap<String, String> paramsInLowerCase = Util.mapToLowerCase(params);
		Map<ErrorCode, String> errors = orderValidation.validateReadParams(paramsInLowerCase);

		if (!errors.isEmpty()) {
			throw new ValidationException(errors, ErrorCode.INVALID_ORDER_REQUEST_PARAMS);
		}

		int offset = OFFSET;
		int limit = LIMIT;

		if (params.containsKey(EntityConstant.OFFSET)) {
			offset = Integer.parseInt(params.get(EntityConstant.OFFSET).get(0));
		}

		if (params.containsKey(EntityConstant.LIMIT)) {
			limit = Integer.parseInt(params.get(EntityConstant.LIMIT).get(0));
		}

		List<OrderModel> orderModels = orderRepository.findAll(offset, limit);
		List<OrderDto> orderDtos = new ArrayList<>(orderModels.size());

		for (OrderModel orderModel : orderModels) {
			orderDtos.add(orderConverter.convertToDto(orderModel));
		}
		return orderDtos;
	}

}
