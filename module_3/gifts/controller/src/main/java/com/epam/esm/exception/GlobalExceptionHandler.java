package com.epam.esm.exception;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
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

	private final MessageSource messageSource;
	private final Environment environment;

	@Autowired
	public GlobalExceptionHandler(MessageSource messageSource, Environment environment) {
		this.messageSource = messageSource;
		this.environment = environment;
	}

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

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiException handleNoHandlerFoundException(HttpRequestMethodNotSupportedException exception) {
		ErrorCode errorCode = ErrorCode.NO_METHOD_FOUND;
		String errorMessage = obtainExceptionMessage(errorCode.getCode());
		String error = exception.getLocalizedMessage();
		return new ApiRootCausesException(errorMessage, errorCode.getCode(), error.toString());
	}

	private ApiException handleException(Map<ErrorCode, String> errors, ErrorCode generalErrorCode,
			String invalidResource) {
		String errorMessage = obtainExceptionMessage(generalErrorCode.getCode(), invalidResource);
		List<String> subErrors = new ArrayList<>();
		if (errors != null) {
			errors.entrySet().forEach(
					error -> subErrors.add(obtainExceptionMessage(error.getKey().getCode(), error.getValue())));
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
		return new ApiRootCausesException(errorMessage, errorCode.getCode(), exception.getOriginalMessage());
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiException handleJsonProcessingException(HttpMessageNotReadableException exception) {
		Throwable cause = exception.getRootCause();
		if (cause == null) {
			return handleException(exception);
		} else if (cause instanceof InvalidFormatException) {
			return handleInvalidFormatException((InvalidFormatException) cause);
		} else if (cause instanceof JsonProcessingException) {
			return handleJsonProcessingException((JsonProcessingException) cause);
		} else {
			return handleException(exception);
		}
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ApiException handleException(Exception exception) {
		ErrorCode errorCode = ErrorCode.INTERNAL_ERROR;
		String errorMessage = obtainExceptionMessage(errorCode.getCode());
		if (Arrays.asList(environment.getActiveProfiles()).contains("dev")) {
			return new ApiRootCausesException(errorMessage, errorCode.getCode(),
					Arrays.toString(exception.getStackTrace()));
		}
		return new ApiException(errorMessage, errorCode.getCode());
	}
}
