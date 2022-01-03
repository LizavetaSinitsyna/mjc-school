package com.epam.esm.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.epam.esm.dto.CertificateDto;
import com.epam.esm.service.CertificateService;

/**
 * Controller for working with certificates.
 * 
 */
@RestController
@RequestMapping("/api/v1/certificates")
public class CertificateController {

	private CertificateService certificateService;

	@Autowired
	public CertificateController(CertificateService certificateService) {
		this.certificateService = certificateService;
	}

	/**
	 * Creates and saves passed certificate.
	 * 
	 * @param certificateDto the certificate to be saved
	 * @return saved certificate
	 */
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public CertificateDto create(@RequestBody CertificateDto certificateDto) {
		CertificateDto createdCertificateDto = certificateService.create(certificateDto);
		HateoasUtil.addLinksToCertificate(createdCertificateDto);
		return createdCertificateDto;
	}

	/**
	 * Reads certificate with passed id.
	 * 
	 * @param id the id of certificate to be read
	 * @return certificate with passed id
	 */
	@GetMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public CertificateDto readById(@PathVariable long id) {
		CertificateDto certificateDto = certificateService.readById(id);
		HateoasUtil.addLinksToCertificate(certificateDto);
		return certificateDto;
	}

	/**
	 * Reads all certificates according to the passed parameters.
	 * 
	 * @param params the parameters which define the choice of certificates and
	 *               their ordering
	 * @return certificates which meet passed parameters
	 */
	@GetMapping
	public ResponseEntity<List<CertificateDto>> readAll(@RequestParam MultiValueMap<String, String> params) {
		List<CertificateDto> certificates = certificateService.readAll(params);
		if (certificates == null || certificates.isEmpty()) {
			return new ResponseEntity<>(certificates, HttpStatus.NO_CONTENT);
		} else {
			for (CertificateDto certificateDto : certificates) {
				HateoasUtil.addLinksToCertificate(certificateDto);
			}
			return new ResponseEntity<>(certificates, HttpStatus.OK);
		}
	}

	/**
	 * Updates certificate fields with passed id using not {@code null} fields of
	 * the passed certificate entity.
	 * 
	 * @param id          the id of the certificate to be updated
	 * @param certificate certificate entity which contains fields with new values
	 *                    to be set
	 * @return updated certificate
	 */
	@PatchMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public CertificateDto updateCertificateFields(@RequestBody CertificateDto certificate, @PathVariable long id) {
		CertificateDto updatedCertificateDto = certificateService.updateCertificateFields(id, certificate);
		HateoasUtil.addLinksToCertificate(updatedCertificateDto);
		return updatedCertificateDto;
	}

	/**
	 * Updates entire certificate with passed id using all fields of the passed
	 * certificate.
	 * 
	 * @param id          the id of the certificate to be updated
	 * @param certificate certificate entity which contains fields with new values
	 *                    to be set
	 * @return updated certificate
	 */
	@PutMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public CertificateDto updateEntireCertificate(@RequestBody CertificateDto certificate, @PathVariable long id) {
		CertificateDto updatedCertificateDto = certificateService.updateEntireCertificate(id, certificate);
		HateoasUtil.addLinksToCertificate(updatedCertificateDto);
		return updatedCertificateDto;
	}

	/**
	 * Deletes certificate with passed id.
	 * 
	 * @param id the id of the certificate to be deleted
	 *
	 */
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable long id) {
		certificateService.delete(id);
	}
}
