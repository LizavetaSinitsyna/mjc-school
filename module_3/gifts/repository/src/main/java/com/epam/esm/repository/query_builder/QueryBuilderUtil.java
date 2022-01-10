package com.epam.esm.repository.query_builder;

public class QueryBuilderUtil {
	/**
	 * Calculates start index for read query.
	 * 
	 * @param pageNumber the pageNumber for showing
	 * @param offset     the limit of items on page
	 * @return calculated start index for read query
	 */
	public static int retrieveStartIndex(int pageNumber, int offset) {
		int startIndex = 0;
		if (pageNumber > 1) {
			startIndex += (pageNumber - 1) * offset;
		}
		return startIndex;
	}

	public static long retrievePageAmount(long totalEntriesAmount, int offset) {
		long additionalPage = totalEntriesAmount % offset > 0 ? 1 : 0;
		return totalEntriesAmount / offset + additionalPage;
	}
}
