package com.epam.esm.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
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
	 * @param tagId the id of the tag for certificates reading
	 * @return certificates with passed tag
	 */
	List<CertificateModel> readByTagId(long tagId);

	/**
	 * Reads certificate with passed name.
	 * 
	 * @param name the name of the certificate to be read
	 * @return certificate with passed name
	 */
	Optional<CertificateModel> findByName(String name);

	/**
	 * Checks whether the certificate with passed name already exists.
	 * 
	 * @param certificateName the name of the certificate to check
	 * @return {@code true} if the the certificate with passed name already exists
	 *         and {@code false} otherwise
	 */
	boolean certificateExistsByName(String certificateName);

	/**
	 * Updates certificate fields with passed id using not {@code null} fields of
	 * the passed certificate.
	 * 
	 * @param certificateToUpdate certificate entity which contains fields with new
	 *                            values to be set
	 * @return updated certificate
	 */
	CertificateModel updateCertificate(CertificateModel certificateToUpdate);

	/**
	 * Reads all certificates according to the passed parameters.
	 * 
	 * @param params the parameters which define the choice of certificates and
	 *               their ordering
	 * @param offset start position for certificates reading
	 * @param limit  amount of certificates to be read
	 * @return certificates which meet passed parameters
	 */
	Page<CertificateModel> findAll(MultiValueMap<String, String> params, int offset, int limit);

	/**
	 * Deletes certificate with passed id.
	 * 
	 * @param id the id of the certificate to be deleted
	 * @return the number of deleted certificates
	 */
	int delete(long id);

	/**
	 * Checks whether certificate with passed id exists.
	 * 
	 * @param certificateId the id of the certificate to be checked
	 * @return {@code true} if the the certificate with passed id already exists and
	 *         {@code false} otherwise
	 */
	boolean certificateExistsById(long certificateId);

	/**
	 * Saves the passed certificates.
	 * 
	 * @param certificateModels the certificates to be saved
	 * @return saved certificates
	 */
	List<CertificateModel> saveCertificates(List<CertificateModel> certificateModels);
}
