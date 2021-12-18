package com.epam.esm.repository;

import java.util.List;
import java.util.Optional;

import com.epam.esm.repository.model.CertificateModel;

/**
 * 
 * Contains methods for working mostly with {@code CertificateModel} entity.
 *
 */
public interface CertificateRepository extends GeneralRepository<CertificateModel> {
	/**
	 * Updates entire certificate with passed id using all fields of passed
	 * certificate.
	 * 
	 * @param certificateModel certificate entity which contains fields with new
	 *                         values to be set
	 * @return updated certificate
	 */
	Optional<CertificateModel> updateEntireCertificate(CertificateModel certificateModel);

	/**
	 * Reads certificates by passed tag id.
	 * 
	 * @param tagId the id of tag for certificates reading
	 * @return certificates with passed tag
	 */
	List<CertificateModel> readByTagId(long tagId);

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
	Optional<CertificateModel> updateCertificateFields(CertificateModel certificateToUpdate);
}
