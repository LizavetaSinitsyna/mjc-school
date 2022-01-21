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
	public static final String PARAMS = "params";
	public static final String OFFSET = "page";
	public static final String LIMIT = "size";
	public static final int DEFAULT_PAGE_NUMBER = 0;
	public static final int DEFAULT_LIMIT = 10;
	public static final int MIN_PAGE_NUMBER = 0;
	public static final int MIN_LIMIT_NUMBER = 1;
	public static final int MAX_LIMIT = 100;
	public static final Set<String> GENERAL_POSSIBLE_READ_PARAMS = new HashSet<String>(
			Arrays.asList(OFFSET, LIMIT));

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
			Arrays.asList(EntityConstant.SEARCH, EntityConstant.ORDER_BY, EntityConstant.TAG, ServiceConstant.OFFSET,
					ServiceConstant.LIMIT));
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
	public static final int USER_MAX_NAME_LENGTH = 20;
	public static final String AUTH_EXCEPTION_MESSAGE = "Forbidden";

	// Role
	public static final String DEFAULT_ROLE_NAME = "ROLE_USER";

	// Order
	public static final int ORDER_UNIQUE_CERTIFICATE_MAX_AMOUNT = 100;
	public static final int ORDER_CERTIFICATES_MAX_AMOUNT = 1000;
	public static final int ORDER_CERTIFICATES_MIN_AMOUNT = 1;

	private ServiceConstant() {

	}
}
