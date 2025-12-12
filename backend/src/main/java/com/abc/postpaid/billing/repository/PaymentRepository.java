package com.abc.postpaid.billing.repository;

import com.abc.postpaid.billing.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByInvoiceInvoiceId(Long invoiceId);
    List<Payment> findByPaymentDateBetween(LocalDate startDate, LocalDate endDate);
}
