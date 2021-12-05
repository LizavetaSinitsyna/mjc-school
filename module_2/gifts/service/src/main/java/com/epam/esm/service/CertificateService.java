package com.epam.esm.service;

import java.util.List;
import java.util.Map;

import com.epam.esm.dto.CertificateDto;

public interface CertificateService {
	CertificateDto create(CertificateDto certificateDto);

	CertificateDto read(long certificateId);

	List<CertificateDto> readAll(Map<String, String> filterParams);

	int delete(long certificateId);

	CertificateDto updateCertificateFields(CertificateDto certificate);

	CertificateDto updateEntireCertificate(CertificateDto certificate);

}
