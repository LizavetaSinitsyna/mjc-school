package com.epam.esm.service;

import java.util.List;

import org.springframework.util.MultiValueMap;

import com.epam.esm.dto.CertificateDto;

/**
 * 
 * Contains methods for working mostly with {@code Certificate} entity.
 *
 */
public interface CertificateService {
	/**
	 * Creates and saves the passed certificate.
	 * 
	 * @param certificateDto the certificate to be saved
	 * @return saved certificate
	 */
	CertificateDto create(CertificateDto certificateDto);

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
	 * @param certificateId the id of certificate to be deleted
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
	 * Reads all certificates according to passed parameters.
	 * 
	 * @param params the parameters which define choice of certificates and their
	 *               ordering
	 * @return certificates which meet passed parameters
	 */
	List<CertificateDto> readAll(MultiValueMap<String, String> params);

}
