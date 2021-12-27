package com.epam.esm.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.util.MultiValueMap;

import com.epam.esm.repository.model.CertificateModel;

/**
 * 
 * Contains methods for working mostly with {@code CertificateModel} entity.
 *
 */
public interface CertificateRepository extends GeneralRepository<CertificateModel> {

	/**
	 * Reads certificates by passed tag id.
	 * 
	 * @param tagId the id of tag for certificates reading
	 * @return certificates with passed tag
	 */
	List<CertificateModel> readByTagId(long tagId);

	/**
	 * Reads entity with passed name.
	 * 
	 * @param name the name of entity to be read
	 * @return entity with passed name
	 */
	Optional<CertificateModel> findByName(String name);

	/**
	 * Checks whether the certificate with passed name already exists.
	 * 
	 * @param certificateName the name of certificate to check
	 * @return {@code true} if the the certificate with passed name already exists
	 *         and {@code false} otherwise
	 */
	boolean certificateExistsByName(String certificateName);

	/**
	 * Updates certificate fields with passed id using not {@code null} fields of
	 * passed certificate entity.
	 * 
	 * @param certificateToUpdate certificate entity which contains fields with new
	 *                            values to be set
	 * @return updated certificate
	 */
	CertificateModel updateCertificate(CertificateModel certificateToUpdate);

	/**
	 * Reads all certificates according to passed parameters.
	 * 
	 * @param params the parameters which define choice of certificates and their
	 *               ordering
	 * @return certificates which meet passed parameters
	 */
	List<CertificateModel> findAll(MultiValueMap<String, String> params, int offset, int limit);

	/**
	 * Deletes entity with passed id.
	 * 
	 * @param id the id of entity to be deleted
	 * @return the number of deleted entities
	 */
	int delete(long id);

	/**
	 * Checks whether certificate with passed id exists.
	 * 
	 * @param certificateId the id of tag to be checked
	 * @return {@code true} if the the certificate with passed id already exists and
	 *         {@code false} otherwise
	 */
	boolean certificateExistsById(long certificateId);
}
