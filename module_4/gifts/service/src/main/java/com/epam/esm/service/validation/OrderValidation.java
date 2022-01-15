package com.epam.esm.service.validation;

import com.epam.esm.dto.OrderCertificateDto;
import com.epam.esm.exception.ErrorCode;
import com.epam.esm.repository.model.EntityConstant;
import com.epam.esm.service.ServiceConstant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

/**
 * Contains methods for order validation.
 *
 */
@Component
public class OrderValidation {

	public OrderValidation() {

	}

	/**
	 * Validates parameters for orders reading.
	 * 
	 * @param paramsInLowerCase the parameters for orders reading
	 * @return {@code Map} of {@code ErrorCode} as key and invalid resource as a
	 *         value for invalid parameters. If all parameters are valid returns
	 *         empty map
	 */
	public Map<ErrorCode, String> validateReadParams(MultiValueMap<String, String> params) {
		ValidationUtil.checkNull(params, ServiceConstant.PARAMS);

		MultiValueMap<String, String> paramsInLowerCase = ValidationUtil.mapToLowerCase(params);
		Map<ErrorCode, String> errors = new HashMap<>();

		if (!ServiceConstant.GENERAL_POSSIBLE_READ_PARAMS.containsAll(paramsInLowerCase.keySet())) {
			errors.put(ErrorCode.INVALID_ORDER_READ_PARAM,
					ServiceConstant.PARAMS + ValidationUtil.ERROR_RESOURCE_DELIMITER + params);
		}

		if (paramsInLowerCase.containsKey(ServiceConstant.OFFSET)) {
			errors.putAll(PaginationValidation.validateOffset(paramsInLowerCase.get(ServiceConstant.OFFSET).get(0)));
		}

		if (paramsInLowerCase.containsKey(ServiceConstant.LIMIT)) {
			errors.putAll(PaginationValidation.validateLimit(paramsInLowerCase.get(ServiceConstant.LIMIT).get(0)));
		}

		return errors;
	}

	/**
	 * Validates requirements for the total amount of unique certificates in the
	 * order and amount of each certificate in the order. Please note that method
	 * doesn't combine equal order certificates from the passed {@code List}, it
	 * already expects to receive the {@code List} with unique certificates.
	 * 
	 * @param orderCertificates the {@code List} of unique certificates in the order
	 *                          for validation
	 * @return {@code Map} of {@code ErrorCode} as key and invalid parameter as a
	 *         value for invalid order certificates. If all order certificates are
	 *         valid returns empty map
	 */
	public Map<ErrorCode, String> validateOrderCertificatesAmountRequirements(
			List<OrderCertificateDto> orderCertificates) {
		ValidationUtil.checkNull(orderCertificates, EntityConstant.ORDER_CERTIFICATES);

		Map<ErrorCode, String> errors = new HashMap<>();
		int orderCertificatesAmount = orderCertificates.size();

		if (orderCertificatesAmount > ServiceConstant.ORDER_UNIQUE_CERTIFICATE_MAX_AMOUNT) {
			errors.put(ErrorCode.INVALID_ORDER_UNIQUE_CERTIFICATES_AMOUNT, EntityConstant.CERTIFICATE_AMOUNT
					+ ValidationUtil.ERROR_RESOURCE_DELIMITER + orderCertificatesAmount);
		}

		for (OrderCertificateDto orderCertificate : orderCertificates) {
			Integer certificateAmount = orderCertificate.getCertificateAmount();
			if (certificateAmount == null || certificateAmount < ServiceConstant.ORDER_CERTIFICATES_MIN_AMOUNT
					|| certificateAmount > ServiceConstant.ORDER_CERTIFICATES_MAX_AMOUNT) {
				StringBuilder errorMessage = new StringBuilder();
				errorMessage.append(EntityConstant.ID + ValidationUtil.ERROR_RESOURCE_DELIMITER
						+ orderCertificate.getCertificate().getId());
				errorMessage.append(ValidationUtil.ERROR_RESOURCES_LIST_DELIMITER);
				errorMessage.append(EntityConstant.CERTIFICATE_AMOUNT + ValidationUtil.ERROR_RESOURCE_DELIMITER
						+ certificateAmount);
				errors.put(ErrorCode.INVALID_ORDER_CERTIFICATE_AMOUNT, errorMessage.toString());
			}
		}
		return errors;
	}
}
