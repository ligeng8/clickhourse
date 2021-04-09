package com.example.demo.model;

import com.example.demo.annotation.ColumnName;
import com.example.demo.annotation.Table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

@Table(database = "facebook")
public class SreReportingHourlyStats {

    String datuid;

    String domain;
    @ColumnName("ad_name")
    String adName;
    @ColumnName("date_str")
    String dateStr;
    String timezone;
    @ColumnName("registration_count")
    Long registrationCount;
    @ColumnName("purchase_count")
    Long purchaseCount;
    @ColumnName("purchase_sum_value")
    BigDecimal purchaseSumValue;
    @ColumnName("data_type")
    String dataType;
    String currency;
    @ColumnName("data_time")
    Date data_time;
    String day;
    Integer hour;
    @ColumnName("create_time")
    Timestamp createTime;


    public String getDatuid() {
        return datuid;
    }

    public void setDatuid(String datuid) {
        this.datuid = datuid;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getAdName() {
        return adName;
    }

    public void setAdName(String adName) {
        this.adName = adName;
    }

    public String getDateStr() {
        return dateStr;
    }

    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public Long getRegistrationCount() {
        return registrationCount;
    }

    public void setRegistrationCount(Long registrationCount) {
        this.registrationCount = registrationCount;
    }

    public Long getPurchaseCount() {
        return purchaseCount;
    }

    public void setPurchaseCount(Long purchaseCount) {
        this.purchaseCount = purchaseCount;
    }

    public BigDecimal getPurchaseSumValue() {
        return purchaseSumValue;
    }

    public void setPurchaseSumValue(BigDecimal purchaseSumValue) {
        this.purchaseSumValue = purchaseSumValue;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Date getData_time() {
        return data_time;
    }

    public void setData_time(Date data_time) {
        this.data_time = data_time;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public Integer getHour() {
        return hour;
    }

    public void setHour(Integer hour) {
        this.hour = hour;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }
}
