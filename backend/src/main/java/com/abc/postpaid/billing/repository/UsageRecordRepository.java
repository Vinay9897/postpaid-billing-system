package com.abc.postpaid.billing.repository;

import com.abc.postpaid.billing.entity.UsageRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface UsageRecordRepository extends JpaRepository<UsageRecord, Long> {
    List<UsageRecord> findByServiceServiceId(Long serviceId);
    List<UsageRecord> findByUsageDateBetween(LocalDate startDate, LocalDate endDate);
}
