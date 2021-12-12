package com.epam.esm.repository;

import java.util.List;

import com.epam.esm.repository.model.CertificateModel;

public interface CertificateRepository extends GeneralRepository<CertificateModel> {

	CertificateModel updateEntireCertificate(CertificateModel certificateModel);

	List<CertificateModel> readByTagId(long tagId);

	boolean certificateExistsByName(String certificateName);

	CertificateModel updateCertificateFields(CertificateModel certificateToUpdate);
}
