package com.abc.postpaid.billing.service;

import com.abc.postpaid.billing.dto.UsageRecordRequest;
import com.abc.postpaid.billing.entity.UsageRecord;
import com.abc.postpaid.billing.repository.UsageRecordRepository;
import com.abc.postpaid.billing.service.impl.UsageRecordServiceImpl;
import com.abc.postpaid.customer.entity.ServiceEntity;
import com.abc.postpaid.customer.repository.ServiceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsageRecordServiceImplTest {

    @Mock
    private UsageRecordRepository usageRecordRepository;

    @Mock
    private ServiceRepository serviceRepository;

    @InjectMocks
    private UsageRecordServiceImpl service;

    @Test
    void createUsageRecord_success() {
        Long serviceId = 11L;
        UsageRecordRequest req = new UsageRecordRequest();
        req.setUsageDate(LocalDate.of(2025,5,1));
        req.setUsageAmount(BigDecimal.valueOf(3.5));
        req.setUnit("GB");

        ServiceEntity s = new ServiceEntity(); s.setServiceId(serviceId);
        UsageRecord saved = new UsageRecord(); saved.setUsageId(200L);

        when(serviceRepository.findById(serviceId)).thenReturn(Optional.of(s));
        when(usageRecordRepository.save(any(UsageRecord.class))).thenReturn(saved);

        Long res = service.createUsageRecord(serviceId, req);
        assertEquals(200L, res);
        verify(usageRecordRepository).save(any(UsageRecord.class));
    }

    @Test
    void createUsageRecord_serviceNotFound_throws() {
        when(serviceRepository.findById(77L)).thenReturn(Optional.empty());
        UsageRecordRequest req = new UsageRecordRequest();
        assertThrows(IllegalArgumentException.class, () -> service.createUsageRecord(77L, req));
    }

    @Test
    void getUsageRecord_and_listMappings() {
        UsageRecord r = new UsageRecord(); r.setUsageId(9L);
        ServiceEntity s = new ServiceEntity(); s.setServiceId(3L);
        r.setService(s);
        r.setUsageDate(LocalDate.of(2025,6,6));
        r.setUsageAmount(BigDecimal.valueOf(7.7));
        r.setUnit("mins");

        when(usageRecordRepository.findById(9L)).thenReturn(Optional.of(r));
        var resp = service.getUsageRecord(9L);
        assertEquals(9L, resp.getUsageId());
        assertEquals(3L, resp.getServiceId());

        when(usageRecordRepository.findByServiceServiceId(3L)).thenReturn(Arrays.asList(r));
        assertEquals(1, service.listUsageRecordsByService(3L).size());
    }

    @Test
    void getUsageRecord_notFound_throws() {
        when(usageRecordRepository.findById(1234L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> service.getUsageRecord(1234L));
    }
}
