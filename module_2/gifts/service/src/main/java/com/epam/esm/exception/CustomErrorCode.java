package com.epam.esm.exception;

public enum CustomErrorCode {
	INVALID_CERTIFICATE_ID(40001, "exception.message.certificate.validation.invalid_id"),
	NO_CERTIFICATE_EXISTS_WITH_REQUIRED_PARAM(20001, "exception.message.certificate.validation.not_exists"),
	INVALID_CERTIFICATE_NAME(40001, "exception.message.certificate.validation.invalid_name"),
	INVALID_CERTIFICATE_DESCRIPTION(40001, "exception.message.certificate.validation.invalid_description"),
	INVALID_CERTIFICATE_PRICE(40001, "exception.message.certificate.validation.invalid_price"),
	INVALID_CERTIFICATE_DURATION(40001, "exception.message.certificate.validation.invalid_duration"),
	NULL_PASSED_PARAMETER(40000, "exception.message.validation.null_parameter"),
	DELETED_CERTIFICATE(20001, "exception.message.certificate.deleted"),
	INVALID_TAG_ID(40002, "xception.message.tag.validation.invalid_id"),
	INVALID_TAG_NAME(40002, "exception.message.tag.validation.invalid_name"),
	DUPLICATED_CERTIFICATE_NAME(40001, "exception.message.certificate.validation.duplicated_name"),
	DUPLICATED_TAG_NAME(40002, "exception.message.tag.validation.duplicated_name");

	private int code;
	private String key;

	CustomErrorCode(int code, String key) {
		this.code = code;
		this.key = key;
	}

	public int getCode() {
		return code;
	}

	public String getKey() {
		return key;
	}

}
