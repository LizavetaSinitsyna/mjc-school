package com.epam.esm.repository.impl;

import java.sql.PreparedStatement;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.util.MultiValueMap;

import com.epam.esm.repository.TagRepository;
import com.epam.esm.repository.mapper.TagRowMapper;
import com.epam.esm.repository.model.TagModel;
import com.epam.esm.repository.query_builder.EntityConstant;
import com.epam.esm.repository.query_builder.SQLUtil;

@Repository
public class TagRepositoryImpl implements TagRepository {
	private static final String INSERT_TAG_QUERY = "INSERT INTO tags (name) VALUES (?)";
	private static final String INSERT_TAGS_FOR_CERTIFICATE_QUERY = "INSERT INTO tags_certificates (certificate_id, tag_id) VALUES (?, ?)";
	private static final String SELECT_TAG_BY_ID_QUERY = "SELECT id, name, is_deleted FROM tags WHERE id = ?";
	private static final String SELECT_TAG_BY_NAME_QUERY = "SELECT id, name, is_deleted FROM tags WHERE name = ?";
	private static final String SELECT_ALL_TAGS_BY_PAGE = "SELECT id, name, is_deleted FROM tags LIMIT ?, ?";
	private static final String SELECT_TAG_BY_CERTIFICATE_ID_QUERY = "SELECT id, name, is_deleted FROM tags INNER JOIN tags_certificates "
			+ "ON id = tag_id WHERE certificate_id = ?";
	private static final String RESTORE_TAG_QUERY = "UPDATE tags SET is_deleted = false WHERE id = ?";
	private static final String REMOVE_TAG__QUERY = "UPDATE tags SET is_deleted = true WHERE id = ?";
	private static final String DELETE_TAGS_FOR_CERTIFICATE_QUERY = "DELETE FROM tags_certificates WHERE certificate_id = ?";

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private TagRowMapper tagRowMapper;

	@Override
	public TagModel create(TagModel tagModel) {
		KeyHolder keyHolder = new GeneratedKeyHolder();

		jdbcTemplate.update(connection -> {
			PreparedStatement preparedStatement = connection.prepareStatement(INSERT_TAG_QUERY,
					PreparedStatement.RETURN_GENERATED_KEYS);
			preparedStatement.setString(1, tagModel.getName());
			return preparedStatement;
		}, keyHolder);

		return readById(keyHolder.getKey().longValue());
	}

	@Override
	public TagModel readById(long tagId) {
		List<TagModel> tagModelList = jdbcTemplate.query(SELECT_TAG_BY_ID_QUERY, tagRowMapper, tagId);

		if (tagModelList.isEmpty()) {
			return null;
		}
		return tagModelList.get(0);
	}

	@Override
	public boolean tagExistsById(long tagId) {
		List<TagModel> tagModelList = jdbcTemplate.query(SELECT_TAG_BY_ID_QUERY, tagRowMapper, tagId);
		return !tagModelList.isEmpty();
	}

	@Override
	public List<TagModel> readByCertificateId(long certificateId) {
		return jdbcTemplate.query(SELECT_TAG_BY_CERTIFICATE_ID_QUERY, tagRowMapper, certificateId);
	}

	@Override
	public TagModel readByName(String tagName) {
		List<TagModel> tagModelList = jdbcTemplate.query(SELECT_TAG_BY_NAME_QUERY, tagRowMapper, tagName);
		if (tagModelList.isEmpty()) {
			return null;
		}
		return tagModelList.get(0);
	}

	@Override
	public boolean tagExistsByName(String tagName) {
		List<TagModel> tagModelList = jdbcTemplate.query(SELECT_TAG_BY_NAME_QUERY, tagRowMapper, tagName);
		return !tagModelList.isEmpty();
	}

	@Override
	public List<TagModel> readAll(MultiValueMap<String, String> params) {
		int pageNumber = Integer.parseInt(params.get(EntityConstant.PAGE).get(0));
		int offset = Integer.parseInt(params.get(EntityConstant.LIMIT).get(0));

		return jdbcTemplate.query(SELECT_ALL_TAGS_BY_PAGE, tagRowMapper, SQLUtil.retrieveStartIndex(pageNumber, offset),
				offset);
	}

	@Override
	public int delete(long tagId) {
		int effectedRows = jdbcTemplate.update(connection -> {
			PreparedStatement preparedStatement = connection.prepareStatement(REMOVE_TAG__QUERY);
			preparedStatement.setLong(1, tagId);
			return preparedStatement;
		});
		return effectedRows;
	}

	@Override
	public int saveTagsForCertificate(long certificateId, List<TagModel> tagModels) {
		int changedRows = 0;
		for (TagModel tagModel : tagModels) {
			changedRows += jdbcTemplate.update(connection -> {
				PreparedStatement preparedStatement = connection.prepareStatement(INSERT_TAGS_FOR_CERTIFICATE_QUERY);
				preparedStatement.setLong(1, certificateId);
				preparedStatement.setLong(2, tagModel.getId());
				return preparedStatement;
			});
		}

		return changedRows;

	}

	@Override
	public TagModel restore(TagModel tagModel) {
		int effectedRows = jdbcTemplate.update(connection -> {
			PreparedStatement preparedStatement = connection.prepareStatement(RESTORE_TAG_QUERY);
			preparedStatement.setLong(1, tagModel.getId());
			return preparedStatement;
		});
		if (effectedRows > 0) {
			tagModel.setDeleted(false);
		}
		return tagModel;
	}

	@Override
	public int deleteAllTagsForCertificate(long certificateId) {
		int effectedRows = jdbcTemplate.update(connection -> {
			PreparedStatement preparedStatement = connection.prepareStatement(DELETE_TAGS_FOR_CERTIFICATE_QUERY);
			preparedStatement.setLong(1, certificateId);
			return preparedStatement;
		});

		return effectedRows;

	}

}
