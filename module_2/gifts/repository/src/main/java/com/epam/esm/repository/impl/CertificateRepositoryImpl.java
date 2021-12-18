package com.epam.esm.repository.impl;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

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

/**
 * 
 * Contains methods implementation for working mostly with
 * {@code CertificateModel} entity.
 *
 */
@Repository
public class CertificateRepositoryImpl implements CertificateRepository {
	private static final String INSERT_QUERY = "INSERT INTO gift_certificates "
			+ "(name, description, price, duration, create_date, last_update_date) VALUES (?, ?, ?, ?, ?, ?)";
	private static final String SELECT_CERTIFICATE_BY_ID_QUERY = "SELECT id, name, description, price, duration, create_date, last_update_date, is_deleted FROM gift_certificates WHERE id = ? AND is_deleted = 0";
	private static final String SELECT_CERTIFICATE_BY_NAME_QUERY = "SELECT id, name, description, price, duration, create_date, last_update_date, is_deleted FROM gift_certificates WHERE name = ? AND is_deleted = 0";
	private static final String SELECT_CERTIFICATE_BY_TAG_ID_QUERY = "SELECT id, name, description, price, duration, create_date, last_update_date, is_deleted FROM gift_certificates INNER JOIN tags_certificates "
			+ "ON id = certificate_id WHERE tag_id = ?";
	private static final String REMOVE_CERTIFICATE_QUERY = "UPDATE gift_certificates SET is_deleted = true WHERE id = ?";
	private static final String UPDATE_ENTIRE_CERTIFICATE_QUERY = "UPDATE gift_certificates SET name = ?, description = ?, price = ?, duration = ?, last_update_date = ? WHERE id = ?";
	private static final String UPDATE_CERTIFICATE_NOT_NULL_QUERY = "UPDATE gift_certificates SET name = IF(? IS NULL, name, ?), description = IF(? IS NULL, description, ?), price = IF(? IS NULL, price, ?), duration = IF(? = 0, duration, ?), last_update_date = ? WHERE id = ?";

	private JdbcTemplate jdbcTemplate;

	private CertificateRowMapper certificateRowMapper;

	private CertificateQueryBuilder certificateQueryBuilder;

	@Autowired
	public CertificateRepositoryImpl(JdbcTemplate jdbcTemplate, CertificateRowMapper certificateRowMapper,
			CertificateQueryBuilder certificateQueryBuilder) {
		this.jdbcTemplate = jdbcTemplate;
		this.certificateRowMapper = certificateRowMapper;
		this.certificateQueryBuilder = certificateQueryBuilder;
	}

	/**
	 * Saves the passed certificate.
	 * 
	 * @param certificateModel the certificate to be saved
	 * @return saved certificate
	 */
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

		certificateModel.setId(keyHolder.getKey().longValue());

		return certificateModel;
	}

	/**
	 * Reads certificate with passed id.
	 * 
	 * @param certificateId the id of certificate to be read
	 * @return certificate with passed id
	 */
	@Override
	public Optional<CertificateModel> readById(long certificateId) {
		List<CertificateModel> certificateModelList = jdbcTemplate.query(SELECT_CERTIFICATE_BY_ID_QUERY,
				certificateRowMapper, certificateId);
		if (certificateModelList.isEmpty()) {
			return Optional.empty();
		}
		return Optional.ofNullable(certificateModelList.get(0));
	}

	/**
	 * Reads certificate with passed name.
	 * 
	 * @param certificateName the name of certificate to be read
	 * @return certificate with passed name
	 */
	@Override
	public Optional<CertificateModel> readByName(String certificateName) {
		List<CertificateModel> certificateModelList = jdbcTemplate.query(SELECT_CERTIFICATE_BY_NAME_QUERY,
				certificateRowMapper, certificateName);
		if (certificateModelList.isEmpty()) {
			return Optional.empty();
		}
		return Optional.ofNullable(certificateModelList.get(0));
	}

	/**
	 * Checks whether the certificate with passed name already exists.
	 * 
	 * @param certificateName the name of certificate to check
	 * @return {@code true} if the the certificate with passed name already exists
	 *         and {@code false} otherwise
	 */
	@Override
	public boolean certificateExistsByName(String certificateName) {
		List<CertificateModel> certificateModelList = jdbcTemplate.query(SELECT_CERTIFICATE_BY_NAME_QUERY,
				certificateRowMapper, certificateName);
		return !certificateModelList.isEmpty();
	}

	/**
	 * Reads all certificates according to passed parameters.
	 * 
	 * @param params the parameters which define choice of certificates and their
	 *               ordering
	 * @return certificates which meet passed parameters
	 */
	@Override
	public List<CertificateModel> readAll(MultiValueMap<String, String> params) {
		return jdbcTemplate.query(certificateQueryBuilder.buildSearchQuery(params), certificateRowMapper);
	}

	/**
	 * Updates entire certificate with passed id using all fields of passed
	 * certificate.
	 * 
	 * @param certificateModel certificate entity which contains fields with new
	 *                         values to be set
	 * @return updated certificate
	 */
	@Override
	public Optional<CertificateModel> updateEntireCertificate(CertificateModel certificateModel) {
		jdbcTemplate.update(connection -> {
			PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_ENTIRE_CERTIFICATE_QUERY);
			preparedStatement.setString(1, certificateModel.getName());
			preparedStatement.setString(2, certificateModel.getDescription());
			preparedStatement.setBigDecimal(3, certificateModel.getPrice());
			preparedStatement.setInt(4, certificateModel.getDuration());
			preparedStatement.setTimestamp(5, Timestamp.valueOf(certificateModel.getLastUpdateDate()));
			preparedStatement.setLong(6, certificateModel.getId());
			return preparedStatement;
		});
		return readById(certificateModel.getId());
	}

	/**
	 * Updates certificate fields with passed id using not {@code null} fields of
	 * passed certificate entity.
	 * 
	 * @param certificateToUpdate certificate entity which contains fields with new
	 *                            values to be set
	 * @return updated certificate
	 */
	@Override
	public Optional<CertificateModel> updateCertificateFields(CertificateModel certificateModel) {
		jdbcTemplate.update(connection -> {
			PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_CERTIFICATE_NOT_NULL_QUERY);
			preparedStatement.setString(1, certificateModel.getName());
			preparedStatement.setString(2, certificateModel.getName());
			preparedStatement.setString(3, certificateModel.getDescription());
			preparedStatement.setString(4, certificateModel.getDescription());
			preparedStatement.setBigDecimal(5, certificateModel.getPrice());
			preparedStatement.setBigDecimal(6, certificateModel.getPrice());
			preparedStatement.setInt(7, certificateModel.getDuration());
			preparedStatement.setInt(8, certificateModel.getDuration());
			preparedStatement.setTimestamp(9, Timestamp.valueOf(certificateModel.getLastUpdateDate()));
			preparedStatement.setLong(10, certificateModel.getId());
			return preparedStatement;
		});
		return readById(certificateModel.getId());
	}

	/**
	 * Reads certificates by passed tag id.
	 * 
	 * @param tagId the id of tag for certificates reading
	 * @return certificates with passed tag
	 */
	@Override
	public List<CertificateModel> readByTagId(long tagId) {
		return jdbcTemplate.query(SELECT_CERTIFICATE_BY_TAG_ID_QUERY, certificateRowMapper, tagId);
	}

	/**
	 * Deletes certificate with passed id.
	 * 
	 * @param id the id of certificate to be deleted
	 * @return the number of deleted certificates
	 */
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
