package com.epam.esm.repository;

import java.util.List;
import java.util.Map;

import com.epam.esm.repository.model.CertificateModel;

public interface CertificateRepository {
	CertificateModel create(CertificateModel certificateModel);

	CertificateModel readById(long certificateId);

	List<CertificateModel> readAll(Map<String, String> filterParams);

	CertificateModel update(CertificateModel certificateDTO);

	int delete(long certificateId);

	CertificateModel readByCertificateName(String certificateName);
}
