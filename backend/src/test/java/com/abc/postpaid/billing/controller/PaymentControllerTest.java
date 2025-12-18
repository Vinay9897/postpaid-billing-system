package com.abc.postpaid.billing.controller;

import com.abc.postpaid.billing.dto.PaymentRequest;
import com.abc.postpaid.billing.dto.PaymentResponse;
import com.abc.postpaid.billing.service.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class PaymentControllerTest {

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private com.abc.postpaid.billing.controller.PaymentController controller;

    private MockMvc mvc;
    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders.standaloneSetup(controller).build();
        mapper.findAndRegisterModules();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
        reset(paymentService);
    }

    private void setAuthPrincipal(String principal, String... roles) {
        SimpleGrantedAuthority auth = new SimpleGrantedAuthority(roles.length > 0 ? roles[0] : "ROLE_CUSTOMER");
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(principal, null, Collections.singletonList(auth));
        SecurityContextHolder.getContext().setAuthentication(token);
    }

    @Test
    void recordPayment_created_forAuthenticated() throws Exception {
        Long invoiceId = 10L;
        PaymentRequest req = new PaymentRequest();
        req.setAmount(new BigDecimal("50.00"));
        req.setPaymentDate(java.time.LocalDate.now());

        when(paymentService.recordPayment(eq(invoiceId), any())).thenReturn(555L);

        setAuthPrincipal("20", "ROLE_CUSTOMER");

        mvc.perform(post("/api/invoices/" + invoiceId + "/payments")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("555")));

        verify(paymentService, times(1)).recordPayment(eq(invoiceId), any());
    }

    @Test
    void recordPayment_401_whenUnauthenticated() throws Exception {
        Long invoiceId = 11L;
        PaymentRequest req = new PaymentRequest();

        SecurityContextHolder.clearContext();

        mvc.perform(post("/api/invoices/" + invoiceId + "/payments")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());

        verify(paymentService, never()).recordPayment(anyLong(), any());
    }

    @Test
    void listPayments_ok_forAuthenticated() throws Exception {
        Long invoiceId = 12L;
        PaymentResponse resp = new PaymentResponse();
        resp.setPaymentId(1L); resp.setInvoiceId(invoiceId); resp.setAmount(new BigDecimal("75.00"));

        when(paymentService.listPaymentsByInvoice(invoiceId)).thenReturn(List.of(resp));

        setAuthPrincipal("2", "ROLE_CUSTOMER");

        mvc.perform(get("/api/invoices/" + invoiceId + "/payments"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("paymentId")));

        verify(paymentService, times(1)).listPaymentsByInvoice(invoiceId);
    }

    @Test
    void listPayments_unauthenticated_401() throws Exception {
        Long invoiceId = 13L;
        SecurityContextHolder.clearContext();

        mvc.perform(get("/api/invoices/" + invoiceId + "/payments"))
                .andExpect(status().isUnauthorized());

        verify(paymentService, never()).listPaymentsByInvoice(anyLong());
    }
}
