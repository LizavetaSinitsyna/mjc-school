package com.epam.esm.repository.impl;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.epam.esm.repository.CertificateRepository;
import com.epam.esm.repository.model.CertificateModel;

@Repository
public class CertificateRepositoryImpl implements CertificateRepository {
	private static final String INSERT_QUERY = "INSERT INTO gift_certificates "
			+ "(name, description, price, duration, create_date, last_update_date) VALUES (?, ?, ?, ?, ?, ?)";
	private static final String SELECT_CERTIFICATE_BY_ID_QUERY = "SELECT * FROM gift_certificates WHERE id = ? AND is_deleted = 0";
	private static final String SELECT_CERTIFICATE_BY_NAME_QUERY = "SELECT * FROM gift_certificates WHERE name = ?";

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public CertificateModel create(CertificateModel certificateModel) {
		KeyHolder keyHolder = new GeneratedKeyHolder();

		jdbcTemplate.update(connection -> {
			PreparedStatement preparedStatement = connection.prepareStatement(INSERT_QUERY,
					PreparedStatement.RETURN_GENERATED_KEYS);
			preparedStatement.setString(1, certificateModel.getName());
			preparedStatement.setString(2, certificateModel.getDescription());
			preparedStatement.setBigDecimal(3, certificateModel.getPrice());
			preparedStatement.setInt(4, certificateModel.getDuration());
			preparedStatement.setTimestamp(5, Timestamp.valueOf(certificateModel.getCreateDate()));
			preparedStatement.setTimestamp(6, Timestamp.valueOf(certificateModel.getLastUpdateDate()));
			return preparedStatement;
		}, keyHolder);
		return readById(keyHolder.getKey().longValue());
	}

	@Override
	public CertificateModel readById(long certificateId) {
		List<CertificateModel> certificateModelList = jdbcTemplate.query(SELECT_CERTIFICATE_BY_ID_QUERY,
				(rs, rowNum) -> {
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
				}, certificateId);
		if (certificateModelList.isEmpty()) {
			return null;
		}
		return certificateModelList.get(0);
	}

	@Override
	public CertificateModel readByCertificateName(String certificateName) {
		List<CertificateModel> certificateModelList = jdbcTemplate.query(SELECT_CERTIFICATE_BY_NAME_QUERY,
				(rs, rowNum) -> {
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
				}, certificateName);
		if (certificateModelList.isEmpty()) {
			return null;
		}
		return certificateModelList.get(0);
	}

	@Override
	public List<CertificateModel> readAll(Map<String, String> filterParams) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CertificateModel update(CertificateModel certificateDTO) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int delete(long certificateId) {
		// TODO Auto-generated method stub
		return 0;
	}

}
