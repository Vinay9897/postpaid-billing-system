package com.abc.postpaid.billing.service.impl;

import com.abc.postpaid.billing.dto.PaymentRequest;
import com.abc.postpaid.billing.dto.PaymentResponse;
import com.abc.postpaid.billing.entity.Invoice;
import com.abc.postpaid.billing.entity.Payment;
import com.abc.postpaid.billing.repository.InvoiceRepository;
import com.abc.postpaid.billing.repository.PaymentRepository;
import com.abc.postpaid.billing.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Override
    @Transactional
    public Long recordPayment(Long invoiceId, PaymentRequest request) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found"));

        Payment payment = new Payment();
        payment.setInvoice(invoice);
        payment.setPaymentDate(request.getPaymentDate());
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(request.getPaymentMethod());

        Payment saved = paymentRepository.save(payment);
        return saved.getPaymentId();
    }

    @Override
    public PaymentResponse getPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));
        return mapToResponse(payment);
    }

    @Override
    public List<PaymentResponse> listPaymentsByInvoice(Long invoiceId) {
        return paymentRepository.findByInvoiceInvoiceId(invoiceId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaymentResponse> listPaymentsByDateRange(LocalDate startDate, LocalDate endDate) {
        return paymentRepository.findByPaymentDateBetween(startDate, endDate).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private PaymentResponse mapToResponse(Payment payment) {
        PaymentResponse resp = new PaymentResponse();
        resp.setPaymentId(payment.getPaymentId());
        resp.setInvoiceId(payment.getInvoice() != null ? payment.getInvoice().getInvoiceId() : null);
        resp.setPaymentDate(payment.getPaymentDate());
        resp.setAmount(payment.getAmount());
        resp.setPaymentMethod(payment.getPaymentMethod());
        return resp;
    }
}
