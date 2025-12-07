package com.abc.postpaid.customer.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.OffsetDateTime;

public class ServiceRequest {
    @NotBlank
    private String serviceType;

    private OffsetDateTime startDate;

    @NotBlank
    private String status;

    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }

    public OffsetDateTime getStartDate() { return startDate; }
    public void setStartDate(OffsetDateTime startDate) { this.startDate = startDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
