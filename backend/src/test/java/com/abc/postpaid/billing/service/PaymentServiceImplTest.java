package com.abc.postpaid.billing.service;

import com.abc.postpaid.billing.dto.PaymentRequest;
import com.abc.postpaid.billing.entity.Invoice;
import com.abc.postpaid.billing.entity.Payment;
import com.abc.postpaid.billing.repository.InvoiceRepository;
import com.abc.postpaid.billing.repository.PaymentRepository;
import com.abc.postpaid.billing.service.impl.PaymentServiceImpl;
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
public class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private InvoiceRepository invoiceRepository;

    @InjectMocks
    private PaymentServiceImpl service;

    @Test
    void recordPayment_success() {
        Long invoiceId = 2L;
        PaymentRequest req = new PaymentRequest();
        req.setPaymentDate(LocalDate.of(2025,3,1));
        req.setAmount(BigDecimal.valueOf(50));
        req.setPaymentMethod("card");

        Invoice inv = new Invoice(); inv.setInvoiceId(invoiceId);
        Payment saved = new Payment(); saved.setPaymentId(77L);

        when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.of(inv));
        when(paymentRepository.save(any(Payment.class))).thenReturn(saved);

        Long res = service.recordPayment(invoiceId, req);
        assertEquals(77L, res);
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    void recordPayment_invoiceNotFound_throws() {
        when(invoiceRepository.findById(999L)).thenReturn(Optional.empty());
        PaymentRequest req = new PaymentRequest();
        assertThrows(IllegalArgumentException.class, () -> service.recordPayment(999L, req));
    }

    @Test
    void getPayment_and_listMappings() {
        Payment p = new Payment(); p.setPaymentId(3L);
        Invoice inv = new Invoice(); inv.setInvoiceId(8L);
        p.setInvoice(inv);
        p.setPaymentDate(LocalDate.of(2025,4,5));
        p.setAmount(BigDecimal.valueOf(12.34));
        p.setPaymentMethod("cash");

        when(paymentRepository.findById(3L)).thenReturn(Optional.of(p));
        var resp = service.getPayment(3L);
        assertEquals(3L, resp.getPaymentId());
        assertEquals(8L, resp.getInvoiceId());

        when(paymentRepository.findByInvoiceInvoiceId(8L)).thenReturn(Arrays.asList(p));
        assertEquals(1, service.listPaymentsByInvoice(8L).size());
    }

    @Test
    void getPayment_notFound_throws() {
        when(paymentRepository.findById(55L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> service.getPayment(55L));
    }
}
