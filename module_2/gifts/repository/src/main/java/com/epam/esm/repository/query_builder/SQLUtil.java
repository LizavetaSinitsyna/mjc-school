package com.epam.esm.repository.query_builder;

/**
 * Contains helper methods for query builder
 * 
 */
public class SQLUtil {
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
}
