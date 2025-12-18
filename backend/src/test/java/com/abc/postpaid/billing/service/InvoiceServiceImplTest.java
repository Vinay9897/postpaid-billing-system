package com.abc.postpaid.billing.service;

import com.abc.postpaid.billing.dto.InvoiceRequest;
import com.abc.postpaid.billing.dto.InvoiceResponse;
import com.abc.postpaid.billing.entity.Invoice;
import com.abc.postpaid.billing.repository.InvoiceRepository;
import com.abc.postpaid.billing.service.impl.InvoiceServiceImpl;
import com.abc.postpaid.customer.entity.Customer;
import com.abc.postpaid.customer.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceImplTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private InvoiceServiceImpl invoiceService;

    private Customer sampleCustomer;

    @BeforeEach
    void setUp() {
        sampleCustomer = new Customer();
        sampleCustomer.setCustomerId(11L);
    }

    @Test
    void createInvoice_success() {
        Long customerId = 11L;
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(sampleCustomer));

        InvoiceRequest req = new InvoiceRequest();
        req.setBillingPeriodStart(LocalDate.of(2025, 1, 1));
        req.setBillingPeriodEnd(LocalDate.of(2025, 1, 31));
        req.setTotalAmount(new BigDecimal("123.45"));
        req.setStatus(null); // should default to unpaid

        when(invoiceRepository.save(any(Invoice.class))).thenAnswer(invocation -> {
            Invoice inv = invocation.getArgument(0);
            inv.setInvoiceId(99L);
            return inv;
        });

        Long id = invoiceService.createInvoice(customerId, req);

        assertEquals(99L, id);
        verify(invoiceRepository, times(1)).save(any(Invoice.class));
    }

    @Test
    void createInvoice_customerNotFound_throws() {
        Long customerId = 42L;
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        InvoiceRequest req = new InvoiceRequest();

        assertThrows(IllegalArgumentException.class, () -> invoiceService.createInvoice(customerId, req));
        verify(invoiceRepository, never()).save(any());
    }

    @Test
    void getInvoice_success_and_mapping() {
        Invoice inv = new Invoice();
        inv.setInvoiceId(7L);
        inv.setCustomer(sampleCustomer);
        inv.setBillingPeriodStart(LocalDate.of(2025, 2, 1));
        inv.setBillingPeriodEnd(LocalDate.of(2025, 2, 28));
        inv.setTotalAmount(new BigDecimal("200.00"));
        inv.setStatus("paid");

        when(invoiceRepository.findById(7L)).thenReturn(Optional.of(inv));

        InvoiceResponse resp = invoiceService.getInvoice(7L);

        assertEquals(7L, resp.getInvoiceId());
        assertEquals(sampleCustomer.getCustomerId(), resp.getCustomerId());
        assertEquals(new BigDecimal("200.00"), resp.getTotalAmount());
        assertEquals("paid", resp.getStatus());
    }

    @Test
    void getInvoice_notFound_throws() {
        when(invoiceRepository.findById(123L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> invoiceService.getInvoice(123L));
    }

    @Test
    void listInvoicesByCustomer_mapsResponses() {
        Invoice inv1 = new Invoice();
        inv1.setInvoiceId(1L);
        inv1.setCustomer(sampleCustomer);
        inv1.setBillingPeriodStart(LocalDate.of(2025, 3, 1));
        inv1.setBillingPeriodEnd(LocalDate.of(2025, 3, 31));
        inv1.setTotalAmount(new BigDecimal("50.00"));
        inv1.setStatus("unpaid");

        when(invoiceRepository.findByCustomerCustomerId(sampleCustomer.getCustomerId()))
                .thenReturn(Arrays.asList(inv1));

        List<InvoiceResponse> list = invoiceService.listInvoicesByCustomer(sampleCustomer.getCustomerId());

        assertEquals(1, list.size());
        InvoiceResponse r = list.get(0);
        assertEquals(1L, r.getInvoiceId());
        assertEquals(sampleCustomer.getCustomerId(), r.getCustomerId());
    }
}
