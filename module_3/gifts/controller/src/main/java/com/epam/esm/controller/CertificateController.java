package com.epam.esm.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.ArrayList;
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

import com.epam.esm.controller.converter.CertificateViewConverter;
import com.epam.esm.controller.converter.PageViewConverter;
import com.epam.esm.controller.view.CertificateView;
import com.epam.esm.controller.view.PageView;
import com.epam.esm.dto.CertificateDto;
import com.epam.esm.dto.PageDto;
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
	private final PageViewConverter<CertificateView, CertificateDto> pageConverter;

	@Autowired
	public CertificateController(CertificateService certificateService,
			PageViewConverter<CertificateView, CertificateDto> pageConverter, CertificateViewConverter certificateConverter) {
		this.certificateService = certificateService;
		this.certificateConverter = certificateConverter;
		this.pageConverter = pageConverter;
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
		CertificateView createdCertificateView = certificateConverter.convertToView(createdCertificateDto);
		HateoasUtil.addLinksToCertificate(createdCertificateView);
		return createdCertificateView;
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
		CertificateView certificateView = certificateConverter.convertToView(certificateDto);
		HateoasUtil.addLinksToCertificate(certificateView);
		return certificateView;
	}

	/**
	 * Reads all certificates according to the passed parameters.
	 * 
	 * @param params the parameters which define the choice of certificates and
	 *               their ordering
	 * @return certificates which meet passed parameters
	 */
	@GetMapping
	public ResponseEntity<PageView<CertificateView>> readAll(@RequestParam MultiValueMap<String, String> params) {
		PageDto<CertificateDto> certificatePage = certificateService.readAll(params);
		List<CertificateDto> certificates = certificatePage.getEntities();

		if (certificatePage == null || certificates == null || certificates.isEmpty()) {
			return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
		} else {
			List<CertificateView> certificatesView = new ArrayList<>(certificates.size());
			certificates.forEach(
					certificateDto -> certificatesView.add(certificateConverter.convertToView(certificateDto)));
			PageView<CertificateView> certificatePageView = pageConverter.convertToView(certificatePage,
					certificatesView);
			certificatesView.forEach(certificateView -> HateoasUtil.addLinksToCertificate(certificateView));
			HateoasUtil.addLinksToPage(certificatePageView,
					linkTo(methodOn(CertificateController.class).readAll(params)));
			return new ResponseEntity<>(certificatePageView, HttpStatus.OK);
		}
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
		CertificateView updatedCertificateView = certificateConverter.convertToView(updatedCertificateDto);
		HateoasUtil.addLinksToCertificate(updatedCertificateView);
		return updatedCertificateView;
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
		CertificateView updatedCertificateView = certificateConverter.convertToView(updatedCertificateDto);
		HateoasUtil.addLinksToCertificate(updatedCertificateView);
		return updatedCertificateView;
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
