package com.epam.esm.repository;

/**
 * Contains constants for project entities (column names, read parameters)
 *
 */
public class EntityConstant {
	private EntityConstant() {

	}

	public static final String PARAMS = "params";
	public static final String OFFSET = "offset";
	public static final String LIMIT = "limit";
	public static final String SEARCH = "search";
	public static final String ORDER = "sort";
	public static final String TAG = "tag";

	public static final String NAME = "name";
	public static final String ID = "id";
	public static final String IS_DELETED = "is_deleted";

	public static final String CERTIFICATE_DESCRIPTION = "description";
	public static final String CERTIFICATE_PRICE = "price";
	public static final String CERTIFICATE_DURATION = "duration";
	public static final String CERTIFICATE_CREATE_DATE = "create_date";
	public static final String CERTIFICATE_LAST_UPDATE_DATE = "lastUpdateDate";
	public static final String CERTIFICATE_TAGS = "tags";

	public static final String CERTIFICATE_ID = "certificate_id";
	public static final String TAG_ID = "tag_id";

	public static final char DESC_SIGN = '-';

}
