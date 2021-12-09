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
import com.epam.esm.repository.mapper.CertificateRowMapper;
import com.epam.esm.repository.model.CertificateModel;
import com.epam.esm.repository.model.TagModel;

@Repository
public class CertificateRepositoryImpl implements CertificateRepository {
	private static final String INSERT_QUERY = "INSERT INTO gift_certificates "
			+ "(name, description, price, duration, create_date, last_update_date) VALUES (?, ?, ?, ?, ?, ?)";
	private static final String SELECT_CERTIFICATE_BY_ID_QUERY = "SELECT id, name, description, price, duration, create_date, last_update_date, is_deleted FROM gift_certificates WHERE id = ? AND is_deleted = 0";
	private static final String SELECT_CERTIFICATE_BY_NAME_QUERY = "SELECT id, name, description, price, duration, create_date, last_update_date, is_deleted FROM gift_certificates WHERE name = ?";
	private static final String SELECT_CERTIFICATE_BY_TAG_ID_QUERY = "SELECT id, name, description, price, duration, create_date, last_update_date, is_deleted FROM gift_certificates INNER JOIN tags_certificates "
			+ "ON id = certificate_id WHERE tag_id = ?";
	private static final String REMOVE_CERTIFICATE__QUERY = "UPDATE gift_certificates SET is_deleted = true WHERE id = ?";
	private static final String UPDATE_CERTIFICATE__QUERY = "UPDATE gift_certificates SET name = ?, description = ?, price = ?, duration = ?, last_update_date = ? WHERE id = ?";

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private CertificateRowMapper certificateRowMapper;

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
				certificateRowMapper, certificateId);
		if (certificateModelList.isEmpty()) {
			return null;
		}
		return certificateModelList.get(0);
	}

	@Override
	public CertificateModel readByCertificateName(String certificateName) {
		List<CertificateModel> certificateModelList = jdbcTemplate.query(SELECT_CERTIFICATE_BY_NAME_QUERY,
				certificateRowMapper, certificateName);
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
	public CertificateModel update(CertificateModel certificateModel) {
		jdbcTemplate.update(connection -> {
			PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_CERTIFICATE__QUERY);
			preparedStatement.setString(1, certificateModel.getName());
			preparedStatement.setString(2, certificateModel.getDescription());
			preparedStatement.setInt(3, certificateModel.getDuration());
			preparedStatement.setBigDecimal(4, certificateModel.getPrice());
			preparedStatement.setTimestamp(5, Timestamp.valueOf(certificateModel.getLastUpdateDate()));
			preparedStatement.setLong(6, certificateModel.getId());
			return preparedStatement;
		});
		return readById(certificateModel.getId());
	}

	@Override
	public List<CertificateModel> readByTagId(long tagId) {
		return jdbcTemplate.query(SELECT_CERTIFICATE_BY_TAG_ID_QUERY, certificateRowMapper, tagId);
	}

	@Override
	public int delete(long certificateId) {
		int effectedRows = jdbcTemplate.update(connection -> {
			PreparedStatement preparedStatement = connection.prepareStatement(REMOVE_CERTIFICATE__QUERY);
			preparedStatement.setLong(1, certificateId);
			return preparedStatement;
		});
		return effectedRows;
	}

}
