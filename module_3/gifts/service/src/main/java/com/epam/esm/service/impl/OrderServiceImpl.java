package com.epam.esm.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import com.epam.esm.repository.model.OrderModel;
import com.epam.esm.repository.model.EntityConstant;
import com.epam.esm.repository.model.UserModel;
import com.epam.esm.service.OrderService;
import com.epam.esm.service.ServiceConstant;
import com.epam.esm.service.converter.OrderConverter;
import com.epam.esm.service.converter.OrderDataConverter;
import com.epam.esm.service.converter.PageConverter;
import com.epam.esm.service.validation.OrderValidation;
import com.epam.esm.service.validation.ValidationUtil;

/**
 * 
 * Contains methods implementation for working mostly with order entities.
 *
 */
@Service
public class OrderServiceImpl implements OrderService {
	private final OrderRepository orderRepository;
	private final UserRepository userRepository;
	private final CertificateRepository certificateRepository;
	private final OrderConverter orderConverter;
	private final OrderDataConverter orderDataConverter;
	private final PageConverter<OrderDto, OrderModel> pageConverter;
	private final OrderValidation orderValidation;

	@Autowired
	public OrderServiceImpl(OrderRepository orderRepository, UserRepository userRepository,
			CertificateRepository certificateRepository, OrderConverter orderConverter,
			OrderDataConverter orderDataConverter, OrderValidation orderValidation,
			PageConverter<OrderDto, OrderModel> pageConverter) {
		this.orderRepository = orderRepository;
		this.userRepository = userRepository;
		this.certificateRepository = certificateRepository;
		this.orderConverter = orderConverter;
		this.orderDataConverter = orderDataConverter;
		this.pageConverter = pageConverter;
		this.orderValidation = orderValidation;
	}

	/**
	 * Reads order with passed id.
	 * 
	 * @param orderId id of the order to be read
	 * @return order with passed id
	 * @throws ValidationException if passed order id is invalid
	 * @throws NotFoundException   if the order with passed id does not exist
	 */
	@Override
	public OrderDto readById(long orderId) {
		if (!ValidationUtil.isPositive(orderId)) {
			throw new ValidationException(EntityConstant.ID + ValidationUtil.ERROR_RESOURCE_DELIMITER + orderId,
					ErrorCode.INVALID_ORDER_ID);
		}

		OrderModel orderModel = orderRepository.findById(orderId)
				.orElseThrow(() -> new NotFoundException(
						EntityConstant.ID + ValidationUtil.ERROR_RESOURCE_DELIMITER + orderId,
						ErrorCode.NO_ORDER_FOUND));

		return orderConverter.convertToDto(orderModel);
	}

	/**
	 * Reads all orders for the specified user according to the passed parameters.
	 * 
	 * @param userId id of the user whose orders should be read
	 * @param params the parameters which define the choice of orders and their
	 *               ordering
	 * 
	 * @return orders for specified user which meet the passed parameters
	 * @throws NotFoundException   if user with passed id does not exist
	 * @throws ValidationException if passed user id or read parameters are invalid
	 */
	@Override
	public Page<OrderDto> readAllByUserId(long userId, MultiValueMap<String, String> params) {
		MultiValueMap<String, String> paramsInLowerCase = ValidationUtil.mapToLowerCase(params);
		if (!ValidationUtil.isPositive(userId)) {
			throw new ValidationException(EntityConstant.ID + ValidationUtil.ERROR_RESOURCE_DELIMITER + userId,
					ErrorCode.INVALID_USER_ID);
		}
		if (!userRepository.userExistsById(userId)) {
			throw new NotFoundException(EntityConstant.ID + ValidationUtil.ERROR_RESOURCE_DELIMITER + userId,
					ErrorCode.NO_USER_FOUND);
		}
		Map<ErrorCode, String> errors = orderValidation.validateReadParams(paramsInLowerCase);

		if (!errors.isEmpty()) {
			throw new ValidationException(errors, ErrorCode.INVALID_ORDER_REQUEST_PARAMS);
		}

		int offset = ServiceConstant.DEFAULT_PAGE_NUMBER;
		int limit = ServiceConstant.DEFAULT_LIMIT;

		if (params.containsKey(ServiceConstant.OFFSET)) {
			offset = Integer.parseInt(params.get(ServiceConstant.OFFSET).get(0));
		}

		if (params.containsKey(ServiceConstant.LIMIT)) {
			limit = Integer.parseInt(params.get(ServiceConstant.LIMIT).get(0));
		}

		Page<OrderModel> pageModel = orderRepository.readAllByUserId(userId, offset, limit);
		List<OrderModel> orderModels = pageModel.getContent();
		List<OrderDto> orderDtos = new ArrayList<>(limit);
		if (orderModels != null) {
			orderModels.forEach(orderModel -> orderDtos.add(orderConverter.convertToDto(orderModel)));
		}

		return pageConverter.convertToDto(pageModel, orderDtos);
	}

	/**
	 * Creates and saves the passed order for the specified user.
	 * 
	 * @param orderDto the order to be saved
	 * @param userId   the id of the user whose order will be saved
	 * @return saved order
	 * @throws NotFoundException   if user with passed id does not exist
	 * @throws ValidationException if passed user id is invalid or passed order
	 *                             contains invalid fields
	 */
	@Override
	public OrderDto create(long userId, OrderDto orderDto) {
		OrderModel savedOrder = orderRepository.save(obtainOrderModelToSave(userId, orderDto));
		return orderConverter.convertToDto(savedOrder);
	}

	/**
	 * Creates and saves the passed orders.
	 * 
	 * @param orderDtos the orders to be saved
	 * @return saved orders
	 * @throws NotFoundException   if the user from the passed order entity does not
	 *                             exist
	 * @throws ValidationException if the id of the user from the passed order
	 *                             entity is invalid or any of passed orders
	 *                             contains invalid fields
	 */
	@Override
	@Transactional
	public List<OrderDto> createOrders(List<OrderDto> orderDtos) {
		List<OrderDto> createdOrders = new ArrayList<>();
		if (orderDtos != null) {
			List<OrderModel> ordersToSave = new ArrayList<>(orderDtos.size());

			orderDtos.forEach(
					orderDto -> ordersToSave.add(obtainOrderModelToSave(orderDto.getUser().getId(), orderDto)));

			List<OrderModel> createdOrderModels = orderRepository.saveOrders(ordersToSave);

			createdOrderModels.forEach(orderModel -> createdOrders.add(orderConverter.convertToDto(orderModel)));
		}
		return createdOrders;
	}

	private OrderModel obtainOrderModelToSave(long userId, OrderDto orderDto) {
		UserModel userModel = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
				EntityConstant.ID + ValidationUtil.ERROR_RESOURCE_DELIMITER + userId, ErrorCode.NO_USER_FOUND));

		Map<ErrorCode, String> errors = checkOrderContentExistance(orderDto);
		if (!errors.isEmpty()) {
			throw new ValidationException(errors, ErrorCode.INVALID_CERTIFICATE);
		}

		List<OrderCertificateDto> orderCertificates = obtainUniqueOrderCertificates(orderDto.getCertificates());
		errors = orderValidation.validateOrderCertificatesAmountRequirements(orderCertificates);
		if (!errors.isEmpty()) {
			throw new ValidationException(errors, ErrorCode.INVALID_CERTIFICATE);
		}

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
		orderToSave.setUser(userModel);
		return orderToSave;
	}

	/**
	 * Reads information about the order with passed id for the specified user.
	 * 
	 * @param userId  id of the user whose order should be read
	 * @param orderId id of the order to be read
	 * @return information about the order with passed id for the specified user
	 * @throws NotFoundException   if the order with passed id does not exist
	 * @throws ValidationException if passed order or user id is invalid
	 */
	@Override
	public OrderDataDto readOrderDataByUserId(long userId, long orderId) {
		if (!ValidationUtil.isPositive(orderId)) {
			throw new ValidationException(EntityConstant.ID + ValidationUtil.ERROR_RESOURCE_DELIMITER + orderId,
					ErrorCode.INVALID_ORDER_ID);
		}

		OrderModel orderModel = orderRepository.findById(orderId)
				.orElseThrow(() -> new NotFoundException(
						EntityConstant.ID + ValidationUtil.ERROR_RESOURCE_DELIMITER + orderId,
						ErrorCode.NO_ORDER_FOUND));

		if (orderModel.getUser().getId() != userId) {
			StringBuilder errorMessage = new StringBuilder();
			errorMessage.append(EntityConstant.USER_ID + ValidationUtil.ERROR_RESOURCE_DELIMITER + userId);
			errorMessage.append(ValidationUtil.ERROR_RESOURCES_LIST_DELIMITER);
			errorMessage.append(EntityConstant.ORDER_ID + ValidationUtil.ERROR_RESOURCE_DELIMITER + orderId);
			throw new ValidationException(errorMessage.toString(), ErrorCode.USER_ID_MISMATCH);
		}

		return orderDataConverter.convertToDto(orderModel);
	}

	/**
	 * Reads all orders according to the passed parameters.
	 * 
	 * @param params the parameters which define the choice of orders and their
	 *               ordering
	 * @return orders which meet passed parameters
	 * @throws ValidationException if passed parameters are invalid
	 */
	@Override
	public Page<OrderDto> readAll(MultiValueMap<String, String> params) {
		MultiValueMap<String, String> paramsInLowerCase = ValidationUtil.mapToLowerCase(params);
		Map<ErrorCode, String> errors = orderValidation.validateReadParams(paramsInLowerCase);

		if (!errors.isEmpty()) {
			throw new ValidationException(errors, ErrorCode.INVALID_ORDER_REQUEST_PARAMS);
		}

		int offset = ServiceConstant.DEFAULT_PAGE_NUMBER;
		int limit = ServiceConstant.DEFAULT_LIMIT;

		if (params.containsKey(ServiceConstant.OFFSET)) {
			offset = Integer.parseInt(params.get(ServiceConstant.OFFSET).get(0));
		}

		if (params.containsKey(ServiceConstant.LIMIT)) {
			limit = Integer.parseInt(params.get(ServiceConstant.LIMIT).get(0));
		}

		Page<OrderModel> pageModel = orderRepository.findAll(offset, limit);
		List<OrderModel> orderModels = pageModel.getContent();
		List<OrderDto> orderDtos = new ArrayList<>(limit);
		if (orderModels != null) {
			orderModels.forEach(orderModel -> orderDtos.add(orderConverter.convertToDto(orderModel)));
		}

		return pageConverter.convertToDto(pageModel, orderDtos);
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

	private Map<ErrorCode, String> checkOrderContentExistance(OrderDto orderDto) {
		ValidationUtil.checkNull(orderDto, EntityConstant.ORDER);
		Map<ErrorCode, String> errors = new HashMap<>();
		List<OrderCertificateDto> orderCertificates = orderDto.getCertificates();
		if (orderCertificates == null || orderCertificates.isEmpty()) {
			errors.put(ErrorCode.NO_ORDER_CERTIFICATES_FOUND,
					EntityConstant.ORDER_CERTIFICATES + ValidationUtil.ERROR_RESOURCE_DELIMITER + orderCertificates);
		} else {
			for (OrderCertificateDto orderCertificate : orderCertificates) {
				CertificateDto certificateDto = orderCertificate.getCertificate();
				ValidationUtil.checkNull(certificateDto, EntityConstant.CERTIFICATE);
				Long certificateId = certificateDto.getId();
				if (!ValidationUtil.isPositive(certificateId)) {
					errors.put(ErrorCode.INVALID_CERTIFICATE_ID,
							EntityConstant.ID + ValidationUtil.ERROR_RESOURCE_DELIMITER + certificateId);
				} else if (!certificateRepository.certificateExistsById(certificateId)) {
					errors.put(ErrorCode.NO_CERTIFICATE_FOUND,
							EntityConstant.ID + ValidationUtil.ERROR_RESOURCE_DELIMITER + certificateId);
				}
			}
		}
		return errors;
	}
}
