package com.epamtc.util;

import java.math.BigDecimal;

import org.apache.commons.lang3.math.NumberUtils;

public class StringUtils {
	private StringUtils() {

	}

	public static boolean isPositiveNumber(String str) {
		if (NumberUtils.isCreatable(str)) {
			Number num = NumberUtils.createNumber(str);
			BigDecimal bidDecimal = new BigDecimal(num.toString());
			return bidDecimal.compareTo(BigDecimal.ZERO) > 0;
		}
		return false;
	}
}