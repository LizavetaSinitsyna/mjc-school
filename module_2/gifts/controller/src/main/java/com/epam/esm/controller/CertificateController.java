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
import com.epam.esm.service.CertificateService;

@RestController
@RequestMapping("/v1/certificates")
public class CertificateController {
	@Autowired
	private CertificateService certificateService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public CertificateDto create(@RequestBody CertificateDto certificateDTO) {
		return certificateService.create(certificateDTO);
	}

	@GetMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public CertificateDto read(@PathVariable long id) {
		return certificateService.read(id);
	}

	//not implemented yet
	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public List<CertificateDto> readAll(@RequestParam Map<String, String> filterParams) {
		return certificateService.readAll(filterParams);
	}
	
	//not implemented yet
	@PatchMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public CertificateDto updateCertificateFields(@RequestBody CertificateDto certificate) {
		return  certificateService.updateCertificateFields(certificate);
	}
	
	//should be reworked
	@PutMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public CertificateDto updateEntireCertificate(@RequestBody CertificateDto certificate, @PathVariable long id) {
		return certificateService.updateEntireCertificate(id, certificate);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable long id) {
		certificateService.delete(id);
	}
}
