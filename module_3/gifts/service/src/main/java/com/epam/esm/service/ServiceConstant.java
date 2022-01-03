package com.epam.esm.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.epam.esm.repository.model.EntityConstant;

/**
 * Contains constants for service layer.
 *
 */
public class ServiceConstant {
	// General
	public static final int OFFSET = 0;
	public static final int LIMIT = 10;
	public static final Set<String> GENERAL_POSSIBLE_READ_PARAMS = new HashSet<String>(
			Arrays.asList(EntityConstant.OFFSET, EntityConstant.LIMIT));

	// Certificate
	public static final int CERTIFICATE_MIN_NAME_LENGTH = 5;
	public static final int CERTIFICATE_MIN_DESCRIPTION_LENGTH = 5;
	public static final int CERTIFICATE_MIN_DURATION = 1;
	public static final int CERTIFICATE_MAX_DURATION = 366;
	public static final int CERTIFICATE_MAX_NAME_LENGTH = 50;
	public static final int CERTIFICATE_MAX_DESCRIPTION_LENGTH = 1000;
	public static final BigDecimal CERTIFICATE_MAX_PRICE = new BigDecimal("5000.00");
	public static final BigDecimal CERTIFICATE_MIN_PRICE = new BigDecimal("0.01");
	public static final int CERTIFICATE_PRICE_SCALE = 2;
	public static final Set<String> CERTIFICATE_POSSIBLE_READ_PARAMS = new HashSet<String>(
			Arrays.asList(EntityConstant.SEARCH, EntityConstant.ORDER_BY, EntityConstant.TAG));
	public static final Set<String> CERTIFICATE_POSSIBLE_SORT_FIELD = new HashSet<String>(Arrays.asList(
			EntityConstant.NAME, EntityConstant.CERTIFICATE_PRICE, EntityConstant.CERTIFICATE_CREATE_DATE,
			EntityConstant.NAME + EntityConstant.DESC_SIGN, EntityConstant.CERTIFICATE_PRICE + EntityConstant.DESC_SIGN,
			EntityConstant.CERTIFICATE_CREATE_DATE + EntityConstant.DESC_SIGN));

	// Tag
	public static final int TAG_MIN_NAME_LENGTH = 2;
	public static final int TAG_MAX_NAME_LENGTH = 25;
	public static final String NO_POPULAR_TAG_FOUND_MESSAGE = "popular tag request";

	// User
	public static final int USER_MIN_NAME_LENGTH = 5;
	public static final int USER_MAX_NAME_LENGTH = 25;

	// Role
	public static final String DEFAULT_ROLE_NAME = "user";

	// Order
	public static final int ORDER_UNIQUE_CERTIFICATE_MAX_AMOUNT = 100;
	public static final int ORDER_CERTIFICATES_MAX_AMOUNT = 1000;
	public static final int ORDER_CERTIFICATES_MIN_AMOUNT = 1;

	private ServiceConstant() {
		CERTIFICATE_POSSIBLE_READ_PARAMS.addAll(GENERAL_POSSIBLE_READ_PARAMS);
	}
}
