package com.epam.esm.repository.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.epam.esm.repository.model.CertificateModel;

@Component
public class CertificateRowMapper implements RowMapper<CertificateModel> {

	@Override
	public CertificateModel mapRow(ResultSet rs, int rowNum) throws SQLException {
		CertificateModel certificateModel = new CertificateModel();
		certificateModel.setId(rs.getLong(1));
		certificateModel.setName(rs.getString(2));
		certificateModel.setDescription(rs.getString(3));
		certificateModel.setPrice(rs.getBigDecimal(4));
		certificateModel.setDuration(rs.getInt(5));
		certificateModel.setCreateDate(rs.getTimestamp(6).toLocalDateTime());
		certificateModel.setLastUpdateDate(rs.getTimestamp(7).toLocalDateTime());
		certificateModel.setDeleted(rs.getBoolean(8));
		return certificateModel;
	}

}
