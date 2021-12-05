package com.epam.esm.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import com.epam.esm.exception.CustomErrorCode;
import com.epam.esm.exception.ValidationException;
import com.epam.esm.service.CertificateService;

@RestController
@RequestMapping("/v1/certificates")
public class CertificateController {
	@Autowired
	private CertificateService certificateService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public CertificateDto create(@RequestBody CertificateDto certificateDTO) {
		CertificateDto createdCertificate = certificateService.create(certificateDTO);
		return createdCertificate;
	}

	@GetMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public CertificateDto read(@PathVariable String id) {
		long certificateId = 0;
		try {
			certificateId = Long.parseLong(id);
		} catch (NumberFormatException e) {
			throw new ValidationException("id = " + id, CustomErrorCode.INVALID_CERTIFICATE_ID);
		}
		CertificateDto certificate = certificateService.read(certificateId);
		return certificate;
	}

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public List<CertificateDto> readAll(@RequestParam Map<String, String> filterParams) {
		List<CertificateDto> certificates = certificateService.readAll(filterParams);
		return certificates;
	}

	@PatchMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public CertificateDto updateCertificateFields(@RequestBody CertificateDto certificate) {
		CertificateDto updatedCertificate = certificateService.updateCertificateFields(certificate);
		return updatedCertificate;
	}

	@PutMapping
	@ResponseStatus(HttpStatus.OK)
	public CertificateDto updateEntireCertificate(@RequestBody CertificateDto certificate) {
		CertificateDto updatedCertificate = certificateService.updateEntireCertificate(certificate);
		return updatedCertificate;
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable long id) {
		certificateService.delete(id);

	}
}
