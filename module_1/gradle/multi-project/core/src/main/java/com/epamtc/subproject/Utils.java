package com.epamtc.subproject;

import com.epamtc.util.StringUtils;

public class Utils {
	private Utils() {

	}

	public static boolean isAllPositiveNumbers(String... str) {
		if (str == null) {
			return false;
		}

		for (String item : str) {
			if (!StringUtils.isPositiveNumber(item)) {
				return false;
			}
		}

		return true;
	}
}
