package com.epam.esm.repository.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.epam.esm.repository.model.TagModel;

@Component
public class TagRowMapper implements RowMapper<TagModel> {

	@Override
	public TagModel mapRow(ResultSet rs, int rowNum) throws SQLException {
		TagModel tagModel = new TagModel();
		tagModel.setId(rs.getLong(1));
		tagModel.setName(rs.getString(2));
		tagModel.setDeleted(rs.getBoolean(3));
		return tagModel;
	}

}
