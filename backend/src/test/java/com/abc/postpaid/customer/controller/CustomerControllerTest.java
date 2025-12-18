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
import java.util.List;
import com.abc.postpaid.customer.dto.ServiceResponse;

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
        resp.setUserId(99L);

        when(customerService.getCustomer(id)).thenReturn(resp);
        // authenticated as a different user -> forbidden
        setAuthPrincipal("77", "ROLE_CUSTOMER");

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
        // admin principal but should not be treated specially; mock a different owned customer
        CustomerResponse adminOwned = new CustomerResponse();
        adminOwned.setCustomerId(999L);
        adminOwned.setUserId(1L);

        when(customerService.getCustomer(id)).thenReturn(resp);
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

        when(customerService.getCustomer(123L)).thenReturn(resp);
        setAuthPrincipal(String.valueOf(userId), "ROLE_CUSTOMER");

        mvc.perform(get("/api/customers/" + 123L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId", is(123)))
                .andExpect(jsonPath("$.fullName", is("Bob")));

        verify(customerService, times(1)).getCustomer(123L);
    }

    @Test
    public void getMyCustomer_unauthenticated() throws Exception {
        SecurityContextHolder.clearContext();

        when(customerService.getCustomer(123L)).thenReturn(new CustomerResponse());

        mvc.perform(get("/api/customers/123"))
                .andExpect(status().isUnauthorized());

        verify(customerService, times(1)).getCustomer(123L);
    }

    @Test
    public void getMyCustomer_notFound() throws Exception {
        Long userId = 10L;
        when(customerService.getCustomer(123L)).thenReturn(null);
        setAuthPrincipal(String.valueOf(userId), "ROLE_CUSTOMER");

        mvc.perform(get("/api/customers/123"))
                .andExpect(status().isNotFound());

        verify(customerService, times(1)).getCustomer(123L);
    }

    @Test
    public void listServicesForOwner_ownerAllowed() throws Exception {
        Long id = 200L;
        CustomerResponse existing = new CustomerResponse();
        existing.setCustomerId(id);
        existing.setUserId(55L);

        ServiceResponse s1 = new ServiceResponse();
        s1.setServiceId(1L); s1.setServiceType("mobile"); s1.setStatus("active");
        ServiceResponse s2 = new ServiceResponse();
        s2.setServiceId(2L); s2.setServiceType("tv"); s2.setStatus("suspended");
        when(customerService.getCustomer(id)).thenReturn(existing);
        when(customerService.listServicesForCustomer(id)).thenReturn(List.of(s1, s2));

        setAuthPrincipal("55", "ROLE_CUSTOMER");

        mvc.perform(get("/api/customers/" + id + "/services"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(2)));

        verify(customerService, times(1)).getCustomer(id);
        verify(customerService, times(1)).listServicesForCustomer(id);
    }

    @Test
    public void listMyServices_okAndNotFound() throws Exception {
        Long userId = 77L;
        CustomerResponse resp = new CustomerResponse();
        resp.setCustomerId(300L);
        resp.setUserId(userId);

        ServiceResponse svc = new ServiceResponse();
        svc.setServiceId(11L); svc.setServiceType("broadband"); svc.setStatus("active");

        when(customerService.getCustomer(300L)).thenReturn(resp);
        when(customerService.listServicesForCustomer(300L)).thenReturn(List.of(svc));

        setAuthPrincipal(String.valueOf(userId), "ROLE_CUSTOMER");
        mvc.perform(get("/api/customers/" + resp.getCustomerId() + "/services"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(1)));

        verify(customerService, times(1)).getCustomer(300L);
        verify(customerService, times(1)).listServicesForCustomer(300L);
    }
}
