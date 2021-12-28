package com.epam.esm.repository.impl;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;

import com.epam.esm.repository.CertificateRepository;
import com.epam.esm.repository.model.CertificateModel;
import com.epam.esm.repository.model.CertificateModel_;
import com.epam.esm.repository.model.EntityConstant;
import com.epam.esm.repository.model.TagModel;
import com.epam.esm.repository.model.TagModel_;

/**
 * 
 * Contains methods implementation for working mostly with
 * {@code CertificateModel} entity.
 *
 */
@Repository
public class CertificateRepositoryImpl implements CertificateRepository {
	private static final List<String> UPDATABLE_FIELDS = Arrays.asList(EntityConstant.CERTIFICATE_DESCRIPTION,
			EntityConstant.CERTIFICATE_DURATION, EntityConstant.CERTIFICATE_PRICE, EntityConstant.NAME,
			EntityConstant.CERTIFICATE_LAST_UPDATE_DATE, EntityConstant.CERTIFICATE_TAGS);
	private static final String PROCENT = "%";

	@PersistenceContext
	private EntityManager entityManager;

	public CertificateRepositoryImpl() {

	}

	/**
	 * Saves the passed certificate.
	 * 
	 * @param certificateModel the certificate to be saved
	 * @return saved certificate
	 */
	@Override
	@Transactional
	public CertificateModel save(CertificateModel certificateModel) {
		entityManager.persist(certificateModel);
		return certificateModel;
	}

	/**
	 * Reads certificate with passed id.
	 * 
	 * @param certificateId the id of certificate to be read
	 * @return certificate with passed id
	 */
	@Override
	public Optional<CertificateModel> findById(long certificateId) {
		try {
			return Optional.of(obtainReadByIdQuery(certificateId).getSingleResult());
		} catch (NoResultException e) {
			return Optional.empty();
		}
	}

	/**
	 * Checks whether certificate with passed id exists.
	 * 
	 * @param certificateId the id of tag to be checked
	 * @return {@code true} if the the certificate with passed id already exists and
	 *         {@code false} otherwise
	 */
	@Override
	public boolean certificateExistsById(long certificateId) {
		try {
			obtainReadByIdQuery(certificateId).getSingleResult();
		} catch (NoResultException e) {
			return false;
		}
		return true;

	}

	private TypedQuery<CertificateModel> obtainReadByIdQuery(long tagId) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<CertificateModel> certificateCriteria = criteriaBuilder.createQuery(CertificateModel.class);
		Root<CertificateModel> certificateRoot = certificateCriteria.from(CertificateModel.class);
		certificateCriteria.select(certificateRoot);

		Predicate isDeletedPredicate = criteriaBuilder.equal(certificateRoot.get(CertificateModel_.isDeleted), false);
		Predicate idPredicate = criteriaBuilder.equal(certificateRoot.get(CertificateModel_.id), tagId);
		certificateCriteria.where(isDeletedPredicate, idPredicate);

		return entityManager.createQuery(certificateCriteria);
	}

	/**
	 * Reads certificate with passed name.
	 * 
	 * @param certificateName the name of certificate to be read
	 * @return certificate with passed name
	 */
	@Override
	public Optional<CertificateModel> findByName(String certificateName) {
		try {
			return Optional.of(obtainReadByNameQuery(certificateName).getSingleResult());
		} catch (NoResultException e) {
			return Optional.empty();
		}
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
		try {
			obtainReadByNameQuery(certificateName).getSingleResult();
		} catch (NoResultException e) {
			return false;
		}
		return true;
	}

	private TypedQuery<CertificateModel> obtainReadByNameQuery(String certificateName) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<CertificateModel> certificateCriteria = criteriaBuilder.createQuery(CertificateModel.class);
		Root<CertificateModel> certificateRoot = certificateCriteria.from(CertificateModel.class);
		certificateCriteria.select(certificateRoot);

		Predicate isDeletedPredicate = criteriaBuilder.equal(certificateRoot.get(CertificateModel_.isDeleted), false);
		Predicate namePredicate = criteriaBuilder.equal(certificateRoot.get(CertificateModel_.name), certificateName);
		certificateCriteria.where(isDeletedPredicate, namePredicate);

		return entityManager.createQuery(certificateCriteria);
	}

	/**
	 * Reads all certificates according to passed parameters.
	 * 
	 * @param params the parameters which define choice of certificates and their
	 *               ordering
	 * @return certificates which meet passed parameters
	 */
	@Override
	public List<CertificateModel> findAll(MultiValueMap<String, String> params, int offset, int limit) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<CertificateModel> certificateCriteria = criteriaBuilder.createQuery(CertificateModel.class);
		Root<CertificateModel> certificateRoot = certificateCriteria.from(CertificateModel.class);
		certificateCriteria.select(certificateRoot);
		certificateCriteria.where(obtainPredicates(params, criteriaBuilder, certificateRoot));
		certificateCriteria.orderBy(obtainOrders(params, criteriaBuilder, certificateRoot));

		TypedQuery<CertificateModel> typedQuery = entityManager.createQuery(certificateCriteria);
		typedQuery.setFirstResult(offset);
		typedQuery.setMaxResults(limit);
		return typedQuery.getResultList();
	}

	private Predicate[] obtainPredicates(MultiValueMap<String, String> params, CriteriaBuilder criteriaBuilder,
			Root<CertificateModel> certificateRoot) {
		List<Predicate> predicates = new ArrayList<>();

		List<String> tags = params.get(EntityConstant.TAG);
		if (tags != null && !tags.isEmpty()) {
			Join<CertificateModel, TagModel> join = certificateRoot.join(CertificateModel_.tags, JoinType.INNER);
			for (String tag : tags) {
				predicates.add(criteriaBuilder.equal(join.get(TagModel_.name), tag));
			}
		}

		List<String> searchPart = params.get(EntityConstant.SEARCH);
		if (searchPart != null) {
			String search = StringUtils.wrap(searchPart.get(0), PROCENT);
			Predicate nameSearchPredicate = criteriaBuilder.like(certificateRoot.get(CertificateModel_.name), search);
			Predicate descriptionSearchPredicate = criteriaBuilder
					.like(certificateRoot.get(CertificateModel_.description), search);
			predicates.add(criteriaBuilder.or(nameSearchPredicate, descriptionSearchPredicate));
		}

		predicates.add(criteriaBuilder.equal(certificateRoot.get(CertificateModel_.isDeleted), false));

		Predicate[] result = new Predicate[predicates.size()];
		return predicates.toArray(result);
	}

	private List<Order> obtainOrders(MultiValueMap<String, String> params, CriteriaBuilder criteriaBuilder,
			Root<CertificateModel> certificateRoot) {
		List<Order> orderConditions = new ArrayList<>();

		List<String> sortConditions = params.get(EntityConstant.ORDER);
		if (sortConditions != null) {
			for (String sortParam : sortConditions) {
				int lastCharIndex = sortParam.length() - 1;
				if (sortParam.charAt(lastCharIndex) == EntityConstant.DESC_SIGN) {
					orderConditions
							.add(criteriaBuilder.desc(certificateRoot.get(sortParam.substring(0, lastCharIndex))));

				} else {
					orderConditions.add(criteriaBuilder.asc(certificateRoot.get(sortParam)));
				}
			}
		}

		return orderConditions;
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
	@Transactional
	public CertificateModel updateCertificate(CertificateModel certificateModel) {
		CertificateModel existedCertificate = entityManager.find(CertificateModel.class, certificateModel.getId());
		setNotNullFields(existedCertificate, certificateModel);
		entityManager.merge(existedCertificate);
		entityManager.refresh(existedCertificate);
		return existedCertificate;
	}

	private void setNotNullFields(CertificateModel existedCertificate, CertificateModel certificateWithUpdatedFields) {
		Class<?> clazz = CertificateModel.class;
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			field.setAccessible(true);
			Object value = null;
			try {
				if (UPDATABLE_FIELDS.contains(field.getName())) {
					value = field.get(certificateWithUpdatedFields);
					if (value != null) {
						field.set(existedCertificate, value);
					}
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new RuntimeException(e.getMessage());
			}
		}
	}

	/**
	 * Reads certificates by passed tag id.
	 * 
	 * @param tagId the id of tag for certificates reading
	 * @return certificates with passed tag
	 */
	@Override
	public List<CertificateModel> readByTagId(long tagId) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<CertificateModel> certificateCriteria = criteriaBuilder.createQuery(CertificateModel.class);
		Root<CertificateModel> certificateRoot = certificateCriteria.from(CertificateModel.class);
		certificateCriteria.select(certificateRoot);

		Join<CertificateModel, TagModel> join = certificateRoot.join(CertificateModel_.tags, JoinType.INNER);
		certificateCriteria.where(criteriaBuilder.equal(join.get(TagModel_.id), tagId));

		return entityManager.createQuery(certificateCriteria).getResultList();
	}

	/**
	 * Deletes certificate with passed id.
	 * 
	 * @param id the id of certificate to be deleted
	 * @return the number of deleted certificates
	 */
	@Override
	@Transactional
	public int delete(long certificateId) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaUpdate<CertificateModel> certificateCriteria = criteriaBuilder
				.createCriteriaUpdate(CertificateModel.class);
		Root<CertificateModel> certificateRoot = certificateCriteria.from(CertificateModel.class);
		certificateCriteria.set(CertificateModel_.isDeleted, true);
		certificateCriteria.where(criteriaBuilder.equal(certificateRoot.get(CertificateModel_.id), certificateId));
		return entityManager.createQuery(certificateCriteria).executeUpdate();
	}

	@Override
	public List<CertificateModel> findAll(int offset, int limit) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<CertificateModel> certificateCriteria = criteriaBuilder.createQuery(CertificateModel.class);
		Root<CertificateModel> tagRoot = certificateCriteria.from(CertificateModel.class);
		certificateCriteria.select(tagRoot);
		certificateCriteria.where(criteriaBuilder.equal(tagRoot.get(CertificateModel_.isDeleted), false));
		TypedQuery<CertificateModel> typedQuery = entityManager.createQuery(certificateCriteria);
		typedQuery.setFirstResult(offset);
		typedQuery.setMaxResults(limit);
		return typedQuery.getResultList();
	}

}
