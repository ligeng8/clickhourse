package com.example.demo.model;

import com.example.demo.annotation.ColumnName;
import com.example.demo.annotation.Table;

@Table(value = "sre_reporting_hourly_stats",database = "facebook")
public class AggressResult extends BaseResult{
	@ColumnName("ad_name")
	private String adName;
	private String currency;
	
	private String domain;
	
	private Long registration;

	public String getAdName() {
		return adName;
	}

	public void setAdName(String adName) {
		this.adName = adName;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}


	public Long getRegistration() {
		return registration;
	}

	public void setRegistration(Long registration) {
		this.registration = registration;
	}
	
	
	
}
