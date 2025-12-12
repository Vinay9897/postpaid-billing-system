package com.abc.postpaid.billing.repository;

import com.abc.postpaid.billing.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByCustomerCustomerId(Long customerId);
    List<Invoice> findByBillingPeriodStartBetween(LocalDate startDate, LocalDate endDate);
    List<Invoice> findByStatus(String status);
}
