package com.epam.esm.repository.impl;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;

import com.epam.esm.repository.CertificateRepository;
import com.epam.esm.repository.model.CertificateModel;
import com.epam.esm.repository.model.EntityConstant;
import com.epam.esm.repository.query_builder.CertificateQueryBuilder;

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
			EntityConstant.CERTIFICATE_TAGS);

	@PersistenceContext
	private EntityManager entityManager;

	@Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
	private int batchSize;

	private final CertificateQueryBuilder certificateQueryBuilder;

	@Autowired
	public CertificateRepositoryImpl(CertificateQueryBuilder certificateQueryBuilder) {
		this.certificateQueryBuilder = certificateQueryBuilder;
	}

	/**
	 * Saves the passed certificate.
	 * 
	 * @param certificateModel the certificate to be saved
	 * @return saved certificate
	 */
	@Override
	public CertificateModel save(CertificateModel certificateModel) {
		entityManager.persist(certificateModel);
		return certificateModel;
	}

	/**
	 * Saves the passed certificates.
	 * 
	 * @param certificateModels the certificates to be saved
	 * @return saved certificates
	 */
	@Override
	@Transactional
	public List<CertificateModel> saveCertificates(List<CertificateModel> certificateModels) {
		int i = 0;
		if (certificateModels != null) {
			for (CertificateModel certificateModel : certificateModels) {
				entityManager.persist(certificateModel);
				++i;
				if (i > 0 && i % batchSize == 0) {
					entityManager.flush();
					entityManager.clear();
				}
			}
		}
		return certificateModels;
	}

	/**
	 * Reads certificate with passed id.
	 * 
	 * @param certificateId the id of the certificate to be read
	 * @return certificate with passed id
	 */
	@Override
	public Optional<CertificateModel> findById(long certificateId) {
		try {
			return Optional
					.of(certificateQueryBuilder.obtainReadByIdQuery(entityManager, certificateId).getSingleResult());
		} catch (NoResultException e) {
			return Optional.empty();
		}
	}

	/**
	 * Checks whether certificate with passed id exists.
	 * 
	 * @param certificateId the id of the certificate to be checked
	 * @return {@code true} if the the certificate with passed id already exists and
	 *         {@code false} otherwise
	 */
	@Override
	public boolean certificateExistsById(long certificateId) {
		try {
			certificateQueryBuilder.obtainReadByIdQuery(entityManager, certificateId).getSingleResult();
		} catch (NoResultException e) {
			return false;
		}
		return true;
	}

	/**
	 * Reads certificate with passed name.
	 * 
	 * @param certificateName the name of the certificate to be read
	 * @return certificate with passed name
	 */
	@Override
	public Optional<CertificateModel> findByName(String certificateName) {
		try {
			return Optional.of(
					certificateQueryBuilder.obtainReadByNameQuery(entityManager, certificateName).getSingleResult());
		} catch (NoResultException e) {
			return Optional.empty();
		}
	}

	/**
	 * Checks whether the certificate with passed name already exists.
	 * 
	 * @param certificateName the name of the certificate to check
	 * @return {@code true} if the the certificate with passed name already exists
	 *         and {@code false} otherwise
	 */
	@Override
	public boolean certificateExistsByName(String certificateName) {
		try {
			certificateQueryBuilder.obtainReadByNameQuery(entityManager, certificateName).getSingleResult();
		} catch (NoResultException e) {
			return false;
		}
		return true;
	}

	/**
	 * Reads all certificates according to the passed parameters.
	 * 
	 * @param params     the parameters which define the choice of certificates and
	 *                   their ordering
	 * @param pageNumber start position for certificates reading
	 * @param limit      amount of certificates to be read
	 * @return certificates which meet passed parameters
	 */
	@Override
	public Page<CertificateModel> findAll(MultiValueMap<String, String> params, int pageNumber, int limit) {
		long totalEntriesAmount = certificateQueryBuilder.obtainCounterQuery(params, entityManager).getSingleResult();
		List<CertificateModel> certificates = certificateQueryBuilder
				.obtainReadAllQuery(entityManager, params, pageNumber, limit).getResultList();

		Pageable pageable = PageRequest.of(pageNumber, limit);
		Page<CertificateModel> pageModel = new PageImpl<>(certificates, pageable, totalEntriesAmount);

		return pageModel;
	}

	/**
	 * Updates certificate fields with passed id using not {@code null} fields of
	 * the passed certificate entity.
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
		return entityManager.merge(existedCertificate);
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
	 * @param tagId the id of the tag for certificates reading
	 * @return certificates with passed tag
	 */
	@Override
	public List<CertificateModel> readByTagId(long tagId) {
		return certificateQueryBuilder.obtainReadByTagIdQuery(entityManager, tagId).getResultList();
	}

	/**
	 * Deletes certificate with passed id.
	 * 
	 * @param id the id of the certificate to be deleted
	 * @return the number of deleted certificates
	 */
	@Override
	@Transactional
	public int delete(long certificateId) {
		return certificateQueryBuilder.obtainDeleteQuery(entityManager, certificateId).executeUpdate();
	}

	/**
	 * Reads all certificates according to the passed parameters.
	 * 
	 * @param pageNumber start position for certificates reading
	 * @param limit      amount of certificates to be read
	 * @return certificates which meet passed parameters
	 */
	@Override
	public Page<CertificateModel> findAll(int pageNumber, int limit) {
		long totalEntriesAmount = certificateQueryBuilder.obtainCounterQuery(null, entityManager).getSingleResult();
		List<CertificateModel> certificates = certificateQueryBuilder
				.obtainReadAllQuery(entityManager, null, pageNumber, limit).getResultList();

		Pageable pageable = PageRequest.of(pageNumber, limit);
		Page<CertificateModel> pageModel = new PageImpl<>(certificates, pageable, totalEntriesAmount);

		return pageModel;
	}
}
