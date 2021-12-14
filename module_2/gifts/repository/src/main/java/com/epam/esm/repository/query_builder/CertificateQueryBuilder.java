package com.epam.esm.repository.query_builder;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

@Component
public class CertificateQueryBuilder {
	private static final String SELECT = "SELECT gift_certificates.id, gift_certificates.name, description, price, duration, create_date,"
			+ " last_update_date, gift_certificates.is_deleted FROM gift_certificates ";
	private static final String TAG_CONDITION = "INNER JOIN tags_certificates ON gift_certificates.id = certificate_id "
			+ "INNER JOIN tags ON tag_id = tags.id WHERE tags.name = %s ";
	private static final String COMMA = ", ";
	private static final String WHERE = "WHERE ";
	private static final String IS_DELETED_CONDITION = "gift_certificates.is_deleted = 0 ";
	private static final String SEARCH_CONDITION = "gift_certificates.name LIKE %s OR gift_certificates.description LIKE %s ";
	private static final String SORT_CONDITION = "ORDER BY ";
	private static final String DESC_CONDITION = " DESC";
	private static final String CONCATENATE_OPERATOR = "AND ";
	private static final String QUOTATION = "\"";
	private static final String PROCENT = "%";
	private static final String LIMIT = "LIMIT %s, %s";
	private static final String SPACE = " ";

	public String buildSearchQuery(MultiValueMap<String, String> params) {
		StringBuilder query = new StringBuilder();
		query.append(SELECT);

		List<String> tags = params.get(EntityConstant.TAG);
		if (tags == null) {
			query.append(WHERE);
		} else {
			query.append(String.format(TAG_CONDITION, StringUtils.wrap(tags.get(0), QUOTATION)));
			query.append(CONCATENATE_OPERATOR);
		}

		query.append(IS_DELETED_CONDITION);

		List<String> searchPart = params.get(EntityConstant.SEARCH);
		if (searchPart != null) {
			query.append(CONCATENATE_OPERATOR);
			String search = StringUtils.wrap(StringUtils.wrap(searchPart.get(0), PROCENT), QUOTATION);
			query.append(String.format(SEARCH_CONDITION, search, search));
		}

		List<String> sortConditions = params.get(EntityConstant.ORDER);
		if (sortConditions != null) {
			query.append(SORT_CONDITION);
			for (int i = 0; i < sortConditions.size(); i++) {
				if (i > 0) {
					query.append(COMMA);
				}
				String sortParam = sortConditions.get(i);
				int lastCharIndex = sortParam.length() - 1;
				if (sortParam.charAt(lastCharIndex) == EntityConstant.DESC_SIGN) {
					query.append(sortParam.substring(0, lastCharIndex));
					query.append(DESC_CONDITION);
				} else {
					query.append(sortParam.substring(0, sortParam.length()));
				}
			}
			query.append(SPACE);
		}

		int pageNumber = Integer.parseInt(params.get(EntityConstant.PAGE).get(0));
		int offset = Integer.parseInt(params.get(EntityConstant.LIMIT).get(0));

		query.append(String.format(LIMIT, SQLUtil.retrieveStartIndex(pageNumber, offset), offset));
		return query.toString();
	}

}
