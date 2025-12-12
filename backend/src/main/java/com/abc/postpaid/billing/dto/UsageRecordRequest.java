package com.abc.postpaid.billing.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class UsageRecordRequest {
    private LocalDate usageDate;
    private BigDecimal usageAmount;
    private String unit;

    public UsageRecordRequest() {}

    public LocalDate getUsageDate() { return usageDate; }
    public void setUsageDate(LocalDate usageDate) { this.usageDate = usageDate; }

    public BigDecimal getUsageAmount() { return usageAmount; }
    public void setUsageAmount(BigDecimal usageAmount) { this.usageAmount = usageAmount; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
}
