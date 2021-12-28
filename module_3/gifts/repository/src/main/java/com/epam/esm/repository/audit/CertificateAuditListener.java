package com.epam.esm.repository.audit;

import java.time.LocalDateTime;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import com.epam.esm.repository.model.CertificateModel;

public class CertificateAuditListener {

	@PrePersist
	public void onPrePersist(CertificateModel certificateModel) {
		LocalDateTime now = LocalDateTime.now();
		certificateModel.setCreateDate(now);
		certificateModel.setLastUpdateDate(now);
		certificateModel.setDeleted(false);

	}

	@PreUpdate
	public void onPreUpdate(CertificateModel certificateModel) {
		certificateModel.setLastUpdateDate(LocalDateTime.now());

	}

}
