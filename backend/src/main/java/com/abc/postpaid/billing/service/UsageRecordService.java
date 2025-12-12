package com.abc.postpaid.billing.service;

import com.abc.postpaid.billing.dto.UsageRecordRequest;
import com.abc.postpaid.billing.dto.UsageRecordResponse;

import java.time.LocalDate;
import java.util.List;

public interface UsageRecordService {
    Long createUsageRecord(Long serviceId, UsageRecordRequest request);
    UsageRecordResponse getUsageRecord(Long usageId);
    List<UsageRecordResponse> listUsageRecordsByService(Long serviceId);
    List<UsageRecordResponse> listUsageRecordsByDateRange(LocalDate startDate, LocalDate endDate);
}
