package com.abc.postpaid.billing.controller;

import com.abc.postpaid.billing.dto.InvoiceRequest;
import com.abc.postpaid.billing.dto.InvoiceResponse;
import com.abc.postpaid.billing.service.InvoiceService;
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
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class InvoiceControllerTest {

    @Mock
    private InvoiceService invoiceService;

    @InjectMocks
    private com.abc.postpaid.billing.controller.InvoiceController controller;

    private MockMvc mvc;
    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders.standaloneSetup(controller).build();
        // enable Java 8 date/time support for tests
        mapper.findAndRegisterModules();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
        reset(invoiceService);
    }

    private void setAuthPrincipal(String principal, String... roles) {
        SimpleGrantedAuthority auth = new SimpleGrantedAuthority(roles.length > 0 ? roles[0] : "ROLE_CUSTOMER");
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(principal, null, Collections.singletonList(auth));
        SecurityContextHolder.getContext().setAuthentication(token);
    }

    @Test
    void listInvoices_adminAllowed() throws Exception {
        Long customerId = 5L;
        InvoiceResponse r = new InvoiceResponse();
        r.setInvoiceId(11L); r.setCustomerId(customerId); r.setBillingPeriodStart(LocalDate.now().minusDays(30)); r.setBillingPeriodEnd(LocalDate.now()); r.setTotalAmount(new BigDecimal("100.00")); r.setStatus("unpaid");

        when(invoiceService.listInvoicesByCustomer(customerId)).thenReturn(List.of(r));

        setAuthPrincipal("1", "ROLE_ADMIN");

        mvc.perform(get("/api/customers/" + customerId + "/invoices"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("invoiceId")));

        verify(invoiceService, times(1)).listInvoicesByCustomer(customerId);
    }

    @Test
    void listInvoices_ownerAllowed() throws Exception {
        Long customerId = 7L;
        InvoiceResponse r = new InvoiceResponse();
        r.setInvoiceId(21L); r.setCustomerId(customerId);

        when(invoiceService.listInvoicesByCustomer(customerId)).thenReturn(List.of(r));

        setAuthPrincipal(String.valueOf(customerId), "ROLE_CUSTOMER");

        mvc.perform(get("/api/customers/" + customerId + "/invoices"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("invoiceId")));

        verify(invoiceService, times(1)).listInvoicesByCustomer(customerId);
    }

    @Test
    void listInvoices_forbiddenWhenNotOwner() throws Exception {
        Long customerId = 8L;
        InvoiceResponse r = new InvoiceResponse();
        r.setInvoiceId(31L); r.setCustomerId(customerId);

        when(invoiceService.listInvoicesByCustomer(customerId)).thenReturn(List.of(r));

        setAuthPrincipal("99", "ROLE_CUSTOMER");

        mvc.perform(get("/api/customers/" + customerId + "/invoices"))
                .andExpect(status().isForbidden());

        // service should not be called when request is forbidden
        verify(invoiceService, never()).listInvoicesByCustomer(customerId);
    }

    @Test
    void listInvoices_unauthenticated_forbidden() throws Exception {
        Long customerId = 9L;
        mvc.perform(get("/api/customers/" + customerId + "/invoices"))
                .andExpect(status().isForbidden());
    }

    @Test
    void createInvoice_adminAllowed() throws Exception {
        Long customerId = 12L;
        InvoiceRequest req = new InvoiceRequest();
        req.setBillingPeriodStart(LocalDate.now().minusDays(30));
        req.setBillingPeriodEnd(LocalDate.now());
        req.setTotalAmount(new BigDecimal("55.00"));
        req.setStatus("unpaid");

        when(invoiceService.createInvoice(eq(customerId), any())).thenReturn(123L);

        setAuthPrincipal("1", "ROLE_ADMIN");

        mvc.perform(post("/api/customers/" + customerId + "/invoices")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("123")));

        verify(invoiceService, times(1)).createInvoice(eq(customerId), any());
    }

    @Test
    void createInvoice_forbidden_ifNotAdmin() throws Exception {
        Long customerId = 12L;
        InvoiceRequest req = new InvoiceRequest();
        setAuthPrincipal(String.valueOf(12L), "ROLE_CUSTOMER");

        mvc.perform(post("/api/customers/" + customerId + "/invoices")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());

        verify(invoiceService, never()).createInvoice(anyLong(), any());
    }

    @Test
    void createInvoice_notFound_whenServiceThrows() throws Exception {
        Long customerId = 13L;
        InvoiceRequest req = new InvoiceRequest();
        when(invoiceService.createInvoice(eq(customerId), any())).thenThrow(new IllegalArgumentException("not_found"));

        setAuthPrincipal("1", "ROLE_ADMIN");

        mvc.perform(post("/api/customers/" + customerId + "/invoices")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isNotFound());

        verify(invoiceService, times(1)).createInvoice(eq(customerId), any());
    }
}
