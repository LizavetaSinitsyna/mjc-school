package com.epam.esm.repository.impl;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.util.MultiValueMap;

import com.epam.esm.repository.CertificateRepository;
import com.epam.esm.repository.mapper.CertificateRowMapper;
import com.epam.esm.repository.model.CertificateModel;
import com.epam.esm.repository.query_builder.CertificateQueryBuilder;

@Repository
public class CertificateRepositoryImpl implements CertificateRepository {
	private static final String INSERT_QUERY = "INSERT INTO gift_certificates "
			+ "(name, description, price, duration, create_date, last_update_date) VALUES (?, ?, ?, ?, ?, ?)";
	private static final String SELECT_CERTIFICATE_BY_ID_QUERY = "SELECT id, name, description, price, duration, create_date, last_update_date, is_deleted FROM gift_certificates WHERE id = ? AND is_deleted = 0";
	private static final String SELECT_CERTIFICATE_BY_NAME_QUERY = "SELECT id, name, description, price, duration, create_date, last_update_date, is_deleted FROM gift_certificates WHERE name = ?";
	private static final String SELECT_CERTIFICATE_BY_TAG_ID_QUERY = "SELECT id, name, description, price, duration, create_date, last_update_date, is_deleted FROM gift_certificates INNER JOIN tags_certificates "
			+ "ON id = certificate_id WHERE tag_id = ?";
	private static final String REMOVE_CERTIFICATE_QUERY = "UPDATE gift_certificates SET is_deleted = true WHERE id = ?";
	private static final String UPDATE_ENTIRE_CERTIFICATE_QUERY = "UPDATE gift_certificates SET name = ?, description = ?, price = ?, duration = ?, last_update_date = ? WHERE id = ?";
	private static final String UPDATE_CERTIFICATE_NOT_NULL_QUERY = "UPDATE gift_certificates SET name = IF(? IS NULL, name, ?), description = IF(? IS NULL, description, ?), price = IF(? IS NULL, price, ?), duration = IF(? IS NULL, duration, ?), last_update_date = ? WHERE id = ?";

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private CertificateRowMapper certificateRowMapper;

	@Autowired
	private CertificateQueryBuilder certificateQueryBuilder;

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
	public CertificateModel readByName(String certificateName) {
		List<CertificateModel> certificateModelList = jdbcTemplate.query(SELECT_CERTIFICATE_BY_NAME_QUERY,
				certificateRowMapper, certificateName);
		if (certificateModelList.isEmpty()) {
			return null;
		}
		return certificateModelList.get(0);
	}

	@Override
	public boolean certificateExistsByName(String certificateName) {
		List<CertificateModel> certificateModelList = jdbcTemplate.query(SELECT_CERTIFICATE_BY_NAME_QUERY,
				certificateRowMapper, certificateName);
		return !certificateModelList.isEmpty();
	}

	@Override
	public List<CertificateModel> readAll(MultiValueMap<String, String> params) {
		return jdbcTemplate.query(certificateQueryBuilder.buildSearchQuery(params), certificateRowMapper);
	}

	@Override
	public CertificateModel updateEntireCertificate(CertificateModel certificateModel) {
		jdbcTemplate.update(connection -> {
			PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_ENTIRE_CERTIFICATE_QUERY);
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
	public CertificateModel updateCertificateFields(CertificateModel certificateModel) {
		jdbcTemplate.update(connection -> {
			PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_CERTIFICATE_NOT_NULL_QUERY);
			preparedStatement.setString(1, certificateModel.getName());
			preparedStatement.setString(2, certificateModel.getName());
			preparedStatement.setString(3, certificateModel.getDescription());
			preparedStatement.setString(4, certificateModel.getDescription());
			preparedStatement.setInt(5, certificateModel.getDuration());
			preparedStatement.setInt(6, certificateModel.getDuration());
			preparedStatement.setBigDecimal(7, certificateModel.getPrice());
			preparedStatement.setBigDecimal(8, certificateModel.getPrice());
			preparedStatement.setTimestamp(9, Timestamp.valueOf(certificateModel.getLastUpdateDate()));
			preparedStatement.setLong(10, certificateModel.getId());
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
			PreparedStatement preparedStatement = connection.prepareStatement(REMOVE_CERTIFICATE_QUERY);
			preparedStatement.setLong(1, certificateId);
			return preparedStatement;
		});
		return effectedRows;
	}

}
