package com.abc.postpaid.customer.dto;

import java.time.OffsetDateTime;

public class ServiceResponse {
    private Long serviceId;
    private Long customerId;
    private String serviceType;
    private OffsetDateTime startDate;
    private String status;

    public Long getServiceId() { return serviceId; }
    public void setServiceId(Long serviceId) { this.serviceId = serviceId; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }

    public OffsetDateTime getStartDate() { return startDate; }
    public void setStartDate(OffsetDateTime startDate) { this.startDate = startDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
