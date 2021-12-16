package com.epam.esm.exception;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {
	private static final String KEY_PREFIX = "exception.";
	private static final String KEY_MIDDLE = ".middle_part";

	@Autowired
	private MessageSource messageSource;

	@ExceptionHandler(ValidationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiException handleValidationException(ValidationException exception) {
		return handleException(exception.getErrors(), exception.getGeneralErrorCode(), exception.getInvalidResource());
	}

	@ExceptionHandler(NullEntityException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiException handleNoExistedEntityException(NullEntityException exception) {
		return handleException(exception.getErrors(), exception.getGeneralErrorCode(), exception.getInvalidResource());
	}

	@ExceptionHandler(DeletedEntityException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ApiException handleDeletedEntityException(DeletedEntityException exception) {
		return handleException(exception.getErrors(), exception.getGeneralErrorCode(), exception.getInvalidResource());
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiException handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception) {
		ErrorCode errorCode = ErrorCode.TYPE_MISMATCH;
		String error = obtainExceptionMessage(errorCode.getCode() + KEY_MIDDLE, exception.getName(),
				exception.getRequiredType().toString());
		String errorMessage = obtainExceptionMessage(errorCode.getCode());
		return new ApiRootCausesException(errorMessage, errorCode.getCode(), error.toString());
	}

	@ExceptionHandler(InvalidFormatException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiException handleInvalidFormatException(InvalidFormatException exception) {
		ErrorCode errorCode = ErrorCode.INVALID_FORMAT;
		String error = obtainExceptionMessage(errorCode.getCode() + KEY_MIDDLE, exception.getValue().toString(),
				exception.getTargetType().toString());
		String errorMessage = obtainExceptionMessage(errorCode.getCode());
		return new ApiRootCausesException(errorMessage, errorCode.getCode(), error.toString());
	}

	private ApiException handleException(Map<ErrorCode, String> errors, ErrorCode generalErrorCode,
			String invalidResource) {
		String errorMessage = obtainExceptionMessage(generalErrorCode.getCode(), invalidResource);
		List<String> subErrors = new ArrayList<>();
		if (errors != null) {
			for (Map.Entry<ErrorCode, String> error : errors.entrySet()) {
				subErrors.add(obtainExceptionMessage(error.getKey().getCode(), error.getValue()));
			}
			return new ApiRootCausesException(errorMessage, generalErrorCode.getCode(), subErrors);
		}
		return new ApiException(errorMessage, generalErrorCode.getCode());
	}

	private String obtainExceptionMessage(String key, Object... insertions) {
		Locale locale = LocaleContextHolder.getLocale();
		return messageSource.getMessage(KEY_PREFIX + key, insertions, locale);
	}

	private String obtainExceptionMessage(String key) {
		Locale locale = LocaleContextHolder.getLocale();
		return messageSource.getMessage(KEY_PREFIX + key, null, locale);
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ApiException handleException(Exception exception) {
		ErrorCode errorCode = ErrorCode.INTERNAL_ERROR;
		String errorMessage = obtainExceptionMessage(errorCode.getCode());
		return new ApiRootCausesException(errorMessage, errorCode.getCode(), exception.getCause().getClass().getName());
	}

	@ExceptionHandler(NotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ApiException handleNotFoundException(NotFoundException exception) {
		return handleException(exception.getErrors(), exception.getGeneralErrorCode(), exception.getInvalidResource());
	}

	@ExceptionHandler(JsonProcessingException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiException handleJsonProcessingException(JsonProcessingException exception) {
		ErrorCode errorCode = ErrorCode.INVALID_JSON_FORMAT;
		String errorMessage = obtainExceptionMessage(errorCode.getCode());
		return new ApiRootCausesException(errorMessage, errorCode.getCode(), exception.getLocalizedMessage());

	}

}
