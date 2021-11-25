package com.epamtc.subproject;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class UtilsTest {

	@Test
	void testIsAllPositiveNumbersReturnsTrue() {
		
		assertTrue(Utils.isAllPositiveNumbers("12", "100"));
	}
	
	@Test
	void testIsAllPositiveNumbersReturnsFalse() {
		
		assertFalse(Utils.isAllPositiveNumbers("12", "-100"));
	}
	
	@Test
	void testIsAllPositiveNumberPassingNullStringReturnsFalse() {
		
		assertFalse(Utils.isAllPositiveNumbers(null, "100"));
	}
	
	@Test
	void testIsAllPositiveNumberPassingNullArrayReturnsFalse() {
		String[] array = null;
		assertFalse(Utils.isAllPositiveNumbers(array));
	}

}
