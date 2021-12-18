package com.epam.esm.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

/**
 * Wrapper for LocalDateTime.class
 *
 */
@Component
public class DateTimeWrapper {
	public DateTimeWrapper() {

	}

	/**
	 * Obtains current date and time.
	 * 
	 * @return current date and time
	 */
	public LocalDateTime obtainCurrentDateTime() {
		LocalDateTime now = LocalDateTime.now();
		return now;
	}
}
