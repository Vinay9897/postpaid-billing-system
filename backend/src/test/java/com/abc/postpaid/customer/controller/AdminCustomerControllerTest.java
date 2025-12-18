package com.abc.postpaid.customer.controller;

import com.abc.postpaid.customer.dto.CustomerRequest;
import com.abc.postpaid.customer.dto.CustomerResponse;
import com.abc.postpaid.customer.dto.ServiceRequest;
import com.abc.postpaid.customer.dto.ServiceResponse;
import com.abc.postpaid.customer.service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AdminCustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "ADMIN")
    void createCustomer_returnsCreated() throws Exception {
        CustomerRequest req = new CustomerRequest();
        req.setUserId(42L);
        req.setFullName("Test User");

        Mockito.when(customerService.createCustomer(any())).thenReturn(123L);

        mockMvc.perform(post("/api/admin/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customer_id", is(123)));

        verify(customerService, times(1)).createCustomer(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void listCustomers_returnsList() throws Exception {
        CustomerResponse a = new CustomerResponse();
        a.setCustomerId(1L); a.setUserId(10L); a.setFullName("A");
        CustomerResponse b = new CustomerResponse();
        b.setCustomerId(2L); b.setUserId(11L); b.setFullName("B");

        Mockito.when(customerService.listCustomers()).thenReturn(List.of(a, b));

        mockMvc.perform(get("/api/admin/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getCustomer_returnsCustomer() throws Exception {
        CustomerResponse resp = new CustomerResponse();
        resp.setCustomerId(5L); resp.setUserId(50L); resp.setFullName("X");

        Mockito.when(customerService.getCustomer(5L)).thenReturn(resp);

        mockMvc.perform(get("/api/admin/customers/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId", is(5)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateCustomer_callsService() throws Exception {
        CustomerRequest req = new CustomerRequest();
        req.setUserId(99L);
        req.setFullName("Updated");

        mockMvc.perform(put("/api/admin/customers/7")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());

        verify(customerService, times(1)).updateCustomer(eq(7L), any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteCustomer_callsService() throws Exception {
        mockMvc.perform(delete("/api/admin/customers/9"))
                .andExpect(status().isNoContent());

        verify(customerService, times(1)).deleteCustomer(9L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createService_returnsCreated() throws Exception {
        ServiceRequest req = new ServiceRequest();
        req.setServiceType("mobile");
        req.setStatus("active");

        Mockito.when(customerService.createServiceForCustomer(eq(3L), any())).thenReturn(77L);

        mockMvc.perform(post("/api/admin/customers/3/services")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.service_id", is(77)));

        verify(customerService, times(1)).createServiceForCustomer(eq(3L), any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void listServices_returnsList() throws Exception {
        ServiceResponse s1 = new ServiceResponse();
        s1.setServiceId(101L); s1.setServiceType("mobile"); s1.setStatus("active");
        ServiceResponse s2 = new ServiceResponse();
        s2.setServiceId(102L); s2.setServiceType("tv"); s2.setStatus("suspended");

        Mockito.when(customerService.listServicesForCustomer(4L)).thenReturn(List.of(s1, s2));

        mockMvc.perform(get("/api/admin/customers/4/services"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void unauthorizedAccess_isForbidden() throws Exception {
        // no auth => should be denied by @PreAuthorize
        mockMvc.perform(get("/api/admin/customers"))
                .andExpect(status().isForbidden());
    }
}
