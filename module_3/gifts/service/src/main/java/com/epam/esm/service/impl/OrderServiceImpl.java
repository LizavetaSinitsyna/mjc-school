package com.epam.esm.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import com.epam.esm.dto.CertificateDto;
import com.epam.esm.dto.OrderCertificateDto;
import com.epam.esm.dto.OrderDataDto;
import com.epam.esm.dto.OrderDto;
import com.epam.esm.exception.ErrorCode;
import com.epam.esm.exception.NotFoundException;
import com.epam.esm.exception.ValidationException;
import com.epam.esm.repository.CertificateRepository;
import com.epam.esm.repository.OrderRepository;
import com.epam.esm.repository.UserRepository;
import com.epam.esm.repository.model.CertificateModel;
import com.epam.esm.repository.model.EntityConstant;
import com.epam.esm.repository.model.OrderModel;
import com.epam.esm.repository.model.UserModel;
import com.epam.esm.service.OrderService;
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
	private OrderValidation orderValidation;

	@Autowired
	public OrderServiceImpl(OrderRepository orderRepository, UserRepository userRepository,
			CertificateRepository certificateRepository, OrderConverter orderConverter,
			OrderDataConverter orderDataConverter, OrderValidation orderValidation) {
		this.orderRepository = orderRepository;
		this.userRepository = userRepository;
		this.certificateRepository = certificateRepository;
		this.orderConverter = orderConverter;
		this.orderDataConverter = orderDataConverter;
		this.orderValidation = orderValidation;
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
		Optional<UserModel> userModel = userRepository.findById(userId);
		if (userModel.isEmpty()) {
			throw new NotFoundException(EntityConstant.ID + Util.ERROR_RESOURCE_DELIMITER + userId,
					ErrorCode.NO_USER_FOUND);
		}

		Map<ErrorCode, String> errors = checkOrderCorrectness(orderDto);

		if (!errors.isEmpty()) {
			throw new ValidationException(errors, ErrorCode.INVALID_CERTIFICATE);
		}

		List<OrderCertificateDto> orderCertificates = obtainUniqueOrderCertificates(orderDto.getCertificates());

		BigDecimal cost = new BigDecimal("0");

		for (OrderCertificateDto orderCertificate : orderCertificates) {
			Long certificateId = orderCertificate.getCertificate().getId();
			Optional<CertificateModel> certificateModelOptional = certificateRepository.findById(certificateId);
			CertificateModel certificateModel = certificateModelOptional.get();

			int certificateAmount = orderCertificate.getCertificateAmount();

			cost = cost.add(certificateModel.getPrice().multiply(new BigDecimal(certificateAmount)));
		}
		
		orderDto.setId(null);
		orderDto.setCertificates(orderCertificates);
		OrderModel orderToSave = orderConverter.convertToModel(orderDto);
		orderToSave.setCost(cost);
		orderToSave.setUser(userModel.get());
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

	private List<OrderCertificateDto> obtainUniqueOrderCertificates(
			List<OrderCertificateDto> initialOrderCertificates) {
		Map<CertificateDto, Integer> orderCertificates = new HashMap<>();
		for (OrderCertificateDto orderCertificateDto : initialOrderCertificates) {
			CertificateDto certificateDto = orderCertificateDto.getCertificate();
			if (orderCertificates.containsKey(certificateDto)) {
				orderCertificates.put(certificateDto,
						orderCertificates.get(certificateDto) + orderCertificateDto.getCertificateAmount());
			} else {
				orderCertificates.put(certificateDto, orderCertificateDto.getCertificateAmount());
			}
		}
		List<OrderCertificateDto> resultOrderCertificates = new ArrayList<>(orderCertificates.size());
		for (Map.Entry<CertificateDto, Integer> orderCertificate : orderCertificates.entrySet()) {
			OrderCertificateDto orderCertificateDto = new OrderCertificateDto();
			orderCertificateDto.setCertificate(orderCertificate.getKey());
			orderCertificateDto.setCertificateAmount(orderCertificate.getValue());
			resultOrderCertificates.add(orderCertificateDto);
		}

		return resultOrderCertificates;

	}

	private Map<ErrorCode, String> checkOrderCorrectness(OrderDto orderDto) {
		Util.checkNull(orderDto, EntityConstant.ORDER);
		Map<ErrorCode, String> errors = new HashMap<>();
		List<OrderCertificateDto> orderCertificates = orderDto.getCertificates();
		if (orderCertificates == null || orderCertificates.isEmpty()) {
			errors.put(ErrorCode.NO_ORDER_CERTIFICATES_FOUND,
					EntityConstant.ORDER_CERTIFICATES + Util.ERROR_RESOURCE_DELIMITER + orderCertificates);
		} else {
			for (OrderCertificateDto orderCertificate : orderCertificates) {
				CertificateDto certificateDto = orderCertificate.getCertificate();
				Util.checkNull(certificateDto, EntityConstant.CERTIFICATE);
				Long certificateId = certificateDto.getId();
				if (!Util.isPositive(certificateId)) {
					errors.put(ErrorCode.INVALID_CERTIFICATE_ID,
							EntityConstant.ID + Util.ERROR_RESOURCE_DELIMITER + certificateId);
				} else if (!certificateRepository.certificateExistsById(certificateId)) {
					errors.put(ErrorCode.NO_CERTIFICATE_FOUND,
							EntityConstant.ID + Util.ERROR_RESOURCE_DELIMITER + certificateId);
				}

				Integer certificateAmount = orderCertificate.getCertificateAmount();
				if (certificateAmount == null || certificateAmount < 1) {
					StringBuilder errorMessage = new StringBuilder();
					errorMessage.append(EntityConstant.ID + Util.ERROR_RESOURCE_DELIMITER + certificateId);
					errorMessage.append(Util.ERROR_RESOURCES_LIST_DELIMITER);
					errorMessage.append(
							EntityConstant.CERTIFICATE_AMOUNT + Util.ERROR_RESOURCE_DELIMITER + certificateAmount);
					errors.put(ErrorCode.NEGATIVE_ORDER_CERTIFICATE_AMOUNT, errorMessage.toString());
				}
			}
		}
		return errors;
	}

}
