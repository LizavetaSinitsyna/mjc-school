package com.epam.esm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
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

import com.epam.esm.controller.assembler.CertificateViewAssembler;
import com.epam.esm.controller.converter.CertificateViewConverter;
import com.epam.esm.controller.view.CertificateView;
import com.epam.esm.dto.CertificateDto;
import com.epam.esm.service.CertificateService;

/**
 * Controller for working with certificates.
 * 
 */
@RestController
@RequestMapping("/api/v1/certificates")
public class CertificateController {

	private final CertificateService certificateService;
	private final CertificateViewConverter certificateConverter;
	private final PagedResourcesAssembler<CertificateDto> pagedResourcesAssembler;
	private final CertificateViewAssembler certificateViewAssembler;

	@Autowired
	public CertificateController(CertificateService certificateService, CertificateViewConverter certificateConverter,
			PagedResourcesAssembler<CertificateDto> pagedResourcesAssembler,
			CertificateViewAssembler certificateViewAssembler) {
		this.certificateService = certificateService;
		this.certificateConverter = certificateConverter;
		this.pagedResourcesAssembler = pagedResourcesAssembler;
		this.certificateViewAssembler = certificateViewAssembler;
	}

	/**
	 * Creates and saves passed certificate.
	 * 
	 * @param certificateView the certificate to be saved
	 * @return saved certificate
	 */
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public CertificateView create(@RequestBody CertificateView certificateView) {
		CertificateDto createdCertificateDto = certificateService
				.create(certificateConverter.convertToDto(certificateView));
		return certificateViewAssembler.toModel(createdCertificateDto);
	}

	/**
	 * Reads certificate with passed id.
	 * 
	 * @param id the id of certificate to be read
	 * @return certificate with passed id
	 */
	@GetMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public CertificateView readById(@PathVariable long id) {
		CertificateDto certificateDto = certificateService.readById(id);
		return certificateViewAssembler.toModel(certificateDto);
	}

	/**
	 * Reads all certificates according to the passed parameters.
	 * 
	 * @param params the parameters which define the choice of certificates and
	 *               their ordering
	 * @return certificates which meet passed parameters
	 */
	@GetMapping
	public ResponseEntity<PagedModel<CertificateView>> readAll(@RequestParam MultiValueMap<String, String> params) {
		Page<CertificateDto> certificatePage = certificateService.readAll(params);
		PagedModel<CertificateView> page = pagedResourcesAssembler.toModel(certificatePage, certificateViewAssembler);
		return new ResponseEntity<>(page, HttpStatus.OK);

	}

	/**
	 * Updates certificate fields with passed id using not {@code null} fields of
	 * the passed certificate entity.
	 * 
	 * @param id              the id of the certificate to be updated
	 * @param certificateView certificate entity which contains fields with new
	 *                        values to be set
	 * @return updated certificate
	 */
	@PatchMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public CertificateView updateCertificateFields(@RequestBody CertificateView certificateView,
			@PathVariable long id) {
		CertificateDto updatedCertificateDto = certificateService.updateCertificateFields(id,
				certificateConverter.convertToDto(certificateView));
		return certificateViewAssembler.toModel(updatedCertificateDto);
	}

	/**
	 * Updates entire certificate with passed id using all fields of the passed
	 * certificate.
	 * 
	 * @param id              the id of the certificate to be updated
	 * @param certificateView certificate entity which contains fields with new
	 *                        values to be set
	 * @return updated certificate
	 */
	@PutMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public CertificateView updateEntireCertificate(@RequestBody CertificateView certificateView,
			@PathVariable long id) {
		CertificateDto updatedCertificateDto = certificateService.updateEntireCertificate(id,
				certificateConverter.convertToDto(certificateView));
		return certificateViewAssembler.toModel(updatedCertificateDto);
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
