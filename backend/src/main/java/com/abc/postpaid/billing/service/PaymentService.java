package com.abc.postpaid.billing.service;

import com.abc.postpaid.billing.dto.PaymentRequest;
import com.abc.postpaid.billing.dto.PaymentResponse;

import java.time.LocalDate;
import java.util.List;

public interface PaymentService {
    Long recordPayment(Long invoiceId, PaymentRequest request);
    PaymentResponse getPayment(Long paymentId);
    List<PaymentResponse> listPaymentsByInvoice(Long invoiceId);
    List<PaymentResponse> listPaymentsByDateRange(LocalDate startDate, LocalDate endDate);
}
