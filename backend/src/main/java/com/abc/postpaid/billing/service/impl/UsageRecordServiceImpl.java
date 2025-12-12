package com.abc.postpaid.billing.service.impl;

import com.abc.postpaid.billing.dto.UsageRecordRequest;
import com.abc.postpaid.billing.dto.UsageRecordResponse;
import com.abc.postpaid.billing.entity.UsageRecord;
import com.abc.postpaid.billing.repository.UsageRecordRepository;
import com.abc.postpaid.billing.service.UsageRecordService;
import com.abc.postpaid.customer.entity.ServiceEntity;
import com.abc.postpaid.customer.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsageRecordServiceImpl implements UsageRecordService {

    @Autowired
    private UsageRecordRepository usageRecordRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Override
    @Transactional
    public Long createUsageRecord(Long serviceId, UsageRecordRequest request) {
        ServiceEntity service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new IllegalArgumentException("Service not found"));

        UsageRecord record = new UsageRecord();
        record.setService(service);
        record.setUsageDate(request.getUsageDate());
        record.setUsageAmount(request.getUsageAmount());
        record.setUnit(request.getUnit());

        UsageRecord saved = usageRecordRepository.save(record);
        return saved.getUsageId();
    }

    @Override
    public UsageRecordResponse getUsageRecord(Long usageId) {
        UsageRecord record = usageRecordRepository.findById(usageId)
                .orElseThrow(() -> new IllegalArgumentException("Usage record not found"));
        return mapToResponse(record);
    }

    @Override
    public List<UsageRecordResponse> listUsageRecordsByService(Long serviceId) {
        return usageRecordRepository.findByServiceServiceId(serviceId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<UsageRecordResponse> listUsageRecordsByDateRange(LocalDate startDate, LocalDate endDate) {
        return usageRecordRepository.findByUsageDateBetween(startDate, endDate).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private UsageRecordResponse mapToResponse(UsageRecord record) {
        UsageRecordResponse resp = new UsageRecordResponse();
        resp.setUsageId(record.getUsageId());
        resp.setServiceId(record.getService() != null ? record.getService().getServiceId() : null);
        resp.setUsageDate(record.getUsageDate());
        resp.setUsageAmount(record.getUsageAmount());
        resp.setUnit(record.getUnit());
        return resp;
    }
}
