package com.epam.esm.exception;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {
	@Autowired
	private ExceptionMessageCreator exceptionMessageCreator;

	private ExceptionDto handleException(CustomErrorCode errorCode, String invalidResource) {
		Locale locale = LocaleContextHolder.getLocale();
		String errorMassage = exceptionMessageCreator.createMessage(errorCode, locale, invalidResource);
		return new ExceptionDto(errorMassage, errorCode.getCode());
	}

	@ExceptionHandler(ValidationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ExceptionDto handleValidationException(ValidationException exception) {
		return handleException(exception.getErrorCode(), exception.getInvalidResource());

	}

	@ExceptionHandler(NullEntityException.class)
	@ResponseStatus(HttpStatus.OK)
	public ExceptionDto handleNoExistedEntityException(NullEntityException exception) {
		return handleException(exception.getErrorCode(), exception.getInvalidResource());

	}

	@ExceptionHandler(DeletedEntityException.class)
	@ResponseStatus(HttpStatus.OK)
	public ExceptionDto handleDeletedEntityException(DeletedEntityException exception) {
		return handleException(exception.getErrorCode(), exception.getInvalidResource());

	}

}
