package com.epam.esm.repository.audit;

import javax.persistence.PrePersist;
import com.epam.esm.repository.model.TagModel;

public class TagAuditListener {
	@PrePersist
	public void onPrePersist(TagModel tagModel) {
		tagModel.setDeleted(false);
	}
}
