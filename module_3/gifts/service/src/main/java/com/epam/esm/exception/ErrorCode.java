package com.epam.esm.exception;

public enum ErrorCode {
	INVALID_CERTIFICATE("100000"), INVALID_CERTIFICATE_ID("100001"), NO_CERTIFICATE_FOUND("100002"),
	INVALID_CERTIFICATE_NAME("100003"), DUPLICATED_CERTIFICATE_NAME("100004"),
	INVALID_CERTIFICATE_DESCRIPTION("100005"), INVALID_CERTIFICATE_PRICE("100006"),
	INVALID_CERTIFICATE_DURATION("100007"), DELETED_CERTIFICATE("100008"), INVALID_CERTIFICATE_READ_PARAM("100009"),
	INVALID_CERTIFICATE_SORT_PARAM("100010"), INVALID_CERTIFICATE_REQUEST_PARAMS("100011"),
	NULL_PASSED_PARAMETER("000001"), TYPE_MISMATCH("000002"), INVALID_FORMAT("000003"), INTERNAL_ERROR("000004"),
	INVALID_OFFSET_FORMAT("000005"), NEGATIVE_OFFSET("000006"), INVALID_LIMIT_FORMAT("000007"),
	NEGATIVE_LIMIT("000008"), INVALID_JSON_FORMAT("000009"), NO_METHOD_FOUND("000010"), NO_HANDLER_FOUND("000011"),
	TOO_LARGE_LIMIT("000012"), INVALID_TAG_ID("200001"), NO_TAG_FOUND("200002"), INVALID_TAG_NAME("200003"),
	DUPLICATED_TAG_NAME("200004"), INVALID_TAG_READ_PARAM("200009"), DELETED_TAG("200008"),
	INVALID_TAG_REQUEST_PARAMS("100011"), INVALID_TAG("200000"), INVALID_USER("300000"), INVALID_USER_ID("300001"),
	NO_USER_FOUND("300002"), INVALID_USER_NAME("300003"), DUPLICATED_USER_NAME("300004"),
	INVALID_USER_READ_PARAM("300009"), INVALID_USER_REQUEST_PARAMS("300011"), INVALID_ORDER("400000"),
	INVALID_ORDER_ID("400001"), NO_ORDER_FOUND("400002"), INVALID_ORDER_READ_PARAM("400009"),
	INVALID_ORDER_REQUEST_PARAMS("400011"), USER_ID_MISMATCH("400012"), NO_ORDER_CERTIFICATES_FOUND("400013"),
	INVALID_ORDER_CERTIFICATE_AMOUNT("400014"), INVALID_ORDER_UNIQUE_CERTIFICATES_AMOUNT("400015");

	private String code;

	ErrorCode(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}
}
