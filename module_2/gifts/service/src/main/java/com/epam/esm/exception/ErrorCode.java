package com.epam.esm.exception;

public enum ErrorCode {
	INVALID_CERTIFICATE("100000"), INVALID_CERTIFICATE_ID("100001"), NO_CERTIFICATE_FOUND("100002"),
	INVALID_CERTIFICATE_NAME("100003"), DUPLICATED_CERTIFICATE_NAME("100004"),
	INVALID_CERTIFICATE_DESCRIPTION("100005"), INVALID_CERTIFICATE_PRICE("100006"),
	INVALID_CERTIFICATE_DURATION("100007"), DELETED_CERTIFICATE("100008"), INVALID_CERTIFICATE_READ_PARAM("100009"),
	INVALID_CERTIFICATE_SORT_PARAM("100010"), INVALID_CERTIFICATE_REQUEST_PARAMS("100011"),
	NULL_PASSED_PARAMETER("000001"), TYPE_MISMATCH("000002"), INVALID_FORMAT("000003"), INTERNAL_ERROR("000004"),
	INVALID_PAGE_FORMAT("000005"), NEGATIVE_PAGE_NUMBER("000006"), INVALID_OFFSET_FORMAT("000007"),
	NEGATIVE_OFFSET("000008"), INVALID_JSON_FORMAT("000009"), INVALID_TAG_ID("200001"), NO_TAG_FOUND("200002"),
	INVALID_TAG_NAME("200003"), DUPLICATED_TAG_NAME("200004"), INVALID_TAG_READ_PARAM("200009"), DELETED_TAG("200008"),
	INVALID_TAG_REQUEST_PARAMS("100011"), INVALID_TAG("200000");

	private String code;

	ErrorCode(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

}
