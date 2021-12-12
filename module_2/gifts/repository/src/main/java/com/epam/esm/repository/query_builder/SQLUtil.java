package com.epam.esm.repository.query_builder;

public class SQLUtil {
	public static int retrieveStartIndex(int pageNumber, int offset) {
		int startIndex = 0;
		if (pageNumber > 1) {
			startIndex += (pageNumber - 1) * offset;
		}
		return startIndex;
	}
}
