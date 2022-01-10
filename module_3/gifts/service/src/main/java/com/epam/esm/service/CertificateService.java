package com.epam.esm.service;

import java.util.List;

import org.springframework.util.MultiValueMap;

import com.epam.esm.dto.CertificateDto;
import com.epam.esm.dto.PageDto;

/**
 * 
 * Contains methods for working mostly with certificate entities.
 *
 */
public interface CertificateService {
	/**
	 * Creates and saves the passed certificate. If tags from the passed certificate
	 * don't exist they will be created and saved as well.
	 * 
	 * @param certificateDto the certificate to be saved
	 * @return saved certificate
	 */
	CertificateDto create(CertificateDto certificateDto);

	/**
	 * Creates and saves the passed certificates. If tags from the passed
	 * certificates don't exist they will be created and saved as well.
	 * 
	 * @param certificateDtos the certificates to be saved
	 * @return saved certificates
	 */
	List<CertificateDto> createCertificates(List<CertificateDto> certificateDtos);

	/**
	 * Reads certificate with passed id.
	 * 
	 * @param certificateId the id of certificate to be read
	 * @return certificate with passed id
	 */
	CertificateDto readById(long certificateId);

	/**
	 * Deletes certificate with passed id.
	 * 
	 * @param certificateId the id of the certificate to be deleted
	 * @return the number of deleted certificates
	 */
	int delete(long certificateId);

	/**
	 * Updates certificate fields with passed id using not {@code null} fields of
	 * passed certificate entity.
	 * 
	 * @param certificateId  the id of certificate to be updated
	 * @param certificateDto certificate entity which contains fields with new
	 *                       values to be set
	 * @return updated certificate
	 */
	CertificateDto updateCertificateFields(long certificateId, CertificateDto certificateDto);

	/**
	 * Updates entire certificate with passed id using all fields of passed
	 * certificate.
	 * 
	 * @param certificateId the id of certificate to be updated
	 * @param certificate   certificate entity which contains fields with new values
	 *                      to be set
	 * @return updated certificate
	 */
	CertificateDto updateEntireCertificate(long certificateId, CertificateDto certificate);

	/**
	 * Reads all certificates according to the passed parameters.
	 * 
	 * @param params the parameters which define the choice of certificates and
	 *               their ordering
	 * @return certificates which meet passed parameters
	 */
	PageDto<CertificateDto> readAll(MultiValueMap<String, String> params);
}
