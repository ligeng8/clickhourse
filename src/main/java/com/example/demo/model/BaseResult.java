package com.example.demo.model;

import java.math.BigDecimal;

import com.example.demo.annotation.ColumnName;

public class BaseResult {
	private Long purchase;
	
	@ColumnName("purchase_value")
	private BigDecimal purchaseValue;

	public Long getPurchase() {
		return purchase;
	}

	public void setPurchase(Long purchase) {
		this.purchase = purchase;
	}

	public BigDecimal getPurchaseValue() {
		return purchaseValue;
	}

	public void setPurchaseValue(BigDecimal purchaseValue) {
		this.purchaseValue = purchaseValue;
	}
	
}
