package com.abc.postpaid.customer.controller;

import com.abc.postpaid.customer.dto.CustomerResponse;
import com.abc.postpaid.customer.service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class CustomerControllerTest {

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private CustomerController controller;

    private MockMvc mvc;
    private ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.standaloneSetup(controller)
                .setHandlerExceptionResolvers(new ExceptionHandlerExceptionResolver())
                .build();
    }

    @AfterEach
    public void teardown() {
        SecurityContextHolder.clearContext();
        reset(customerService);
    }

    private void setAuthPrincipal(String principal, String... roles) {
        SimpleGrantedAuthority auth = new SimpleGrantedAuthority(roles.length > 0 ? roles[0] : "ROLE_CUSTOMER");
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(principal, null, Collections.singletonList(auth));
        SecurityContextHolder.getContext().setAuthentication(token);
    }

    @Test
    public void getCustomer_ownerAllowed() throws Exception {
        Long id = 123L;
        CustomerResponse resp = new CustomerResponse();
        resp.setCustomerId(id);
        resp.setUserId(10L);
        resp.setFullName("Alice");

        when(customerService.getCustomer(id)).thenReturn(resp);
        setAuthPrincipal("10", "ROLE_CUSTOMER");

        mvc.perform(get("/api/customers/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId", is(id.intValue())))
                .andExpect(jsonPath("$.fullName", is("Alice")));

        verify(customerService, times(1)).getCustomer(id);
    }

    @Test
    public void getCustomer_otherForbidden() throws Exception {
        Long id = 123L;
        CustomerResponse resp = new CustomerResponse();
        resp.setCustomerId(id);
        resp.setUserId(10L);

        when(customerService.getCustomer(id)).thenReturn(resp);
        setAuthPrincipal("99", "ROLE_CUSTOMER");

        mvc.perform(get("/api/customers/" + id))
                .andExpect(status().isForbidden());

        verify(customerService, times(1)).getCustomer(id);
    }

    @Test
    public void getCustomer_adminIsNotAllowed() throws Exception {
        Long id = 123L;
        CustomerResponse resp = new CustomerResponse();
        resp.setCustomerId(id);
        resp.setUserId(10L);

        when(customerService.getCustomer(id)).thenReturn(resp);
        // admin principal but should not be treated specially
        setAuthPrincipal("1", "ROLE_ADMIN");

        mvc.perform(get("/api/customers/" + id))
                .andExpect(status().isForbidden());

        verify(customerService, times(1)).getCustomer(id);
    }

    @Test
    public void getMyCustomer_ok() throws Exception {
        Long userId = 10L;
        CustomerResponse resp = new CustomerResponse();
        resp.setCustomerId(123L);
        resp.setUserId(userId);
        resp.setFullName("Bob");

        when(customerService.getCustomerByUserId(userId)).thenReturn(resp);
        setAuthPrincipal(String.valueOf(userId), "ROLE_CUSTOMER");

        mvc.perform(get("/api/customers/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(userId.intValue())))
                .andExpect(jsonPath("$.fullName", is("Bob")));

        verify(customerService, times(1)).getCustomerByUserId(userId);
    }

    @Test
    public void getMyCustomer_unauthenticated() throws Exception {
        SecurityContextHolder.clearContext();

        mvc.perform(get("/api/customers/me"))
                .andExpect(status().isUnauthorized());

        verify(customerService, never()).getCustomerByUserId(anyLong());
    }

    @Test
    public void getMyCustomer_notFound() throws Exception {
        Long userId = 10L;
        when(customerService.getCustomerByUserId(userId)).thenThrow(new IllegalArgumentException("not_found"));
        setAuthPrincipal(String.valueOf(userId), "ROLE_CUSTOMER");

        mvc.perform(get("/api/customers/me"))
                .andExpect(status().isNotFound());

        verify(customerService, times(1)).getCustomerByUserId(userId);
    }

    @Test
    public void updateCustomer_ownerAllowed() throws Exception {
        Long id = 123L;
        CustomerResponse existing = new CustomerResponse();
        existing.setCustomerId(id);
        existing.setUserId(10L);

        when(customerService.getCustomer(id)).thenReturn(existing);
        setAuthPrincipal("10", "ROLE_CUSTOMER");

        String body = "{\"fullName\":\"New Name\",\"phoneNumber\":\"+123\"}";

        mvc.perform(put("/api/customers/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());

        ArgumentCaptor<Long> idCap = ArgumentCaptor.forClass(Long.class);
        verify(customerService, times(1)).updateCustomer(idCap.capture(), any());
        assertEquals(id, idCap.getValue());
    }

    @Test
    public void updateCustomer_forbidden() throws Exception {
        Long id = 123L;
        CustomerResponse existing = new CustomerResponse();
        existing.setCustomerId(id);
        existing.setUserId(10L);

        when(customerService.getCustomer(id)).thenReturn(existing);
        setAuthPrincipal("99", "ROLE_CUSTOMER");

        String body = "{\"fullName\":\"New Name\"}";

        mvc.perform(put("/api/customers/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden());

        verify(customerService, never()).updateCustomer(anyLong(), any());
    }

    @Test
    public void deleteCustomer_ownerAllowed() throws Exception {
        Long id = 123L;
        CustomerResponse existing = new CustomerResponse();
        existing.setCustomerId(id);
        existing.setUserId(10L);

        when(customerService.getCustomer(id)).thenReturn(existing);
        setAuthPrincipal("10", "ROLE_CUSTOMER");

        mvc.perform(delete("/api/customers/" + id))
                .andExpect(status().isNoContent());

        verify(customerService, times(1)).deleteCustomer(id);
    }

    @Test
    public void deleteCustomer_forbidden() throws Exception {
        Long id = 123L;
        CustomerResponse existing = new CustomerResponse();
        existing.setCustomerId(id);
        existing.setUserId(10L);

        when(customerService.getCustomer(id)).thenReturn(existing);
        setAuthPrincipal("99", "ROLE_CUSTOMER");

        mvc.perform(delete("/api/customers/" + id))
                .andExpect(status().isForbidden());

        verify(customerService, never()).deleteCustomer(anyLong());
    }
}
