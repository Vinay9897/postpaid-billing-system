package com.abc.postpaid.billing.service.impl;

import com.abc.postpaid.billing.dto.InvoiceRequest;
import com.abc.postpaid.billing.dto.InvoiceResponse;
import com.abc.postpaid.billing.entity.Invoice;
import com.abc.postpaid.billing.repository.InvoiceRepository;
import com.abc.postpaid.billing.service.InvoiceService;
import com.abc.postpaid.customer.entity.Customer;
import com.abc.postpaid.customer.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    @Transactional
    public Long createInvoice(Long customerId, InvoiceRequest request) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        Invoice invoice = new Invoice();
        invoice.setCustomer(customer);
        invoice.setBillingPeriodStart(request.getBillingPeriodStart());
        invoice.setBillingPeriodEnd(request.getBillingPeriodEnd());
        invoice.setTotalAmount(request.getTotalAmount());
        invoice.setStatus(request.getStatus() != null ? request.getStatus() : "unpaid");

        Invoice saved = invoiceRepository.save(invoice);
        return saved.getInvoiceId();
    }

    @Override
    public InvoiceResponse getInvoice(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found"));
        return mapToResponse(invoice);
    }

    @Override
    public List<InvoiceResponse> listInvoicesByCustomer(Long customerId) {
        return invoiceRepository.findByCustomerCustomerId(customerId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<InvoiceResponse> listInvoicesByDateRange(LocalDate startDate, LocalDate endDate) {
        return invoiceRepository.findByBillingPeriodStartBetween(startDate, endDate).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<InvoiceResponse> listInvoicesByStatus(String status) {
        return invoiceRepository.findByStatus(status).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private InvoiceResponse mapToResponse(Invoice invoice) {
        InvoiceResponse resp = new InvoiceResponse();
        resp.setInvoiceId(invoice.getInvoiceId());
        resp.setCustomerId(invoice.getCustomer() != null ? invoice.getCustomer().getCustomerId() : null);
        resp.setBillingPeriodStart(invoice.getBillingPeriodStart());
        resp.setBillingPeriodEnd(invoice.getBillingPeriodEnd());
        resp.setTotalAmount(invoice.getTotalAmount());
        resp.setStatus(invoice.getStatus());
        return resp;
    }
}
