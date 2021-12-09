package com.epam.esm.exception;

public enum ErrorCode {
	INVALID_CERTIFICATE("100000"), INVALID_CERTIFICATE_ID("100001"),
	NO_CERTIFICATE_EXISTS_WITH_REQUIRED_PARAM("100002"), INVALID_CERTIFICATE_NAME("100003"),
	DUPLICATED_CERTIFICATE_NAME("100004"), INVALID_CERTIFICATE_DESCRIPTION("100005"),
	INVALID_CERTIFICATE_PRICE("100006"), INVALID_CERTIFICATE_DURATION("100007"), DELETED_CERTIFICATE("100008"),
	NULL_PASSED_PARAMETER("000001"), TYPE_MISMATCH("000002"), INVALID_FORMAT("000003"), INTERNAL_ERROR("000004"),
	INVALID_TAG_ID("200001"), INVALID_TAG_NAME("200003"), DUPLICATED_TAG_NAME("200004");

	private String code;

	ErrorCode(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

}
