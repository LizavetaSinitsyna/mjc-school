package com.epamtc.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StringUtilsTest {
	@Test
	void isPositiveNumberReturnsTrue() {
		assertTrue(StringUtils.isPositiveNumber("10"), "isPositiveNumber() should return 'true'");
	}
}
