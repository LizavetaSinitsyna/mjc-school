package com.epam.esm.repository.audit;

import java.time.LocalDateTime;
import javax.persistence.PrePersist;
import com.epam.esm.repository.model.OrderModel;

public class OrderAuditListener {
	@PrePersist
	public void onPrePersist(OrderModel orderModel) {
		orderModel.setDate(LocalDateTime.now());
	}
}
