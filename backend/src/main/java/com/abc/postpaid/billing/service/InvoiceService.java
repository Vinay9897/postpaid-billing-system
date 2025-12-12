package com.abc.postpaid.billing.service;

import com.abc.postpaid.billing.dto.InvoiceRequest;
import com.abc.postpaid.billing.dto.InvoiceResponse;

import java.time.LocalDate;
import java.util.List;

public interface InvoiceService {
    Long createInvoice(Long customerId, InvoiceRequest request);
    InvoiceResponse getInvoice(Long invoiceId);
    List<InvoiceResponse> listInvoicesByCustomer(Long customerId);
    List<InvoiceResponse> listInvoicesByDateRange(LocalDate startDate, LocalDate endDate);
    List<InvoiceResponse> listInvoicesByStatus(String status);
}
