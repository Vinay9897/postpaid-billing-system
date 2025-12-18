package com.abc.postpaid.billing.controller;

import com.abc.postpaid.billing.dto.UsageRecordRequest;
import com.abc.postpaid.billing.dto.UsageRecordResponse;
import com.abc.postpaid.billing.service.UsageRecordService;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UsageControllerTest {

    @Mock
    private UsageRecordService usageRecordService;

    @InjectMocks
    private com.abc.postpaid.billing.controller.UsageController controller;

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
        reset(usageRecordService);
    }

    private void setAuthPrincipal(String principal, String... roles) {
        SimpleGrantedAuthority auth = new SimpleGrantedAuthority(roles.length > 0 ? roles[0] : "ROLE_CUSTOMER");
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(principal, null, Collections.singletonList(auth));
        SecurityContextHolder.getContext().setAuthentication(token);
    }

    @Test
    void getUsageRecords_returnsList() throws Exception {
        Long serviceId = 3L;
        UsageRecordResponse r = new UsageRecordResponse();
        r.setUsageId(1L); r.setServiceId(serviceId); r.setUsageDate(LocalDate.now()); r.setUsageAmount(new BigDecimal("10")); r.setUnit("GB");

        when(usageRecordService.listUsageRecordsByService(serviceId)).thenReturn(List.of(r));

        mvc.perform(get("/api/services/" + serviceId + "/usage"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("usageId")));

        verify(usageRecordService, times(1)).listUsageRecordsByService(serviceId);
    }

    @Test
    void createUsageRecord_adminAllowed() throws Exception {
        Long serviceId = 4L;
        UsageRecordRequest req = new UsageRecordRequest();
        req.setUsageDate(LocalDate.now());
        req.setUsageAmount(new BigDecimal("5"));
        req.setUnit("min");

        when(usageRecordService.createUsageRecord(eq(serviceId), any())).thenReturn(77L);

        setAuthPrincipal("1", "ROLE_ADMIN");

        mvc.perform(post("/api/services/" + serviceId + "/usage")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("77")));

        verify(usageRecordService, times(1)).createUsageRecord(eq(serviceId), any());
    }

    @Test
    void createUsageRecord_forbidden_forNonAdmin() throws Exception {
        Long serviceId = 5L;
        UsageRecordRequest req = new UsageRecordRequest();
        req.setUsageDate(LocalDate.now());
        req.setUsageAmount(new BigDecimal("2"));

        setAuthPrincipal("50", "ROLE_CUSTOMER");

        mvc.perform(post("/api/services/" + serviceId + "/usage")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());

        verify(usageRecordService, never()).createUsageRecord(anyLong(), any());
    }

    @Test
    void createUsageRecord_notFound_whenServiceThrows() throws Exception {
        Long serviceId = 6L;
        UsageRecordRequest req = new UsageRecordRequest();
        req.setUsageDate(LocalDate.now());
        req.setUsageAmount(new BigDecimal("2"));

        when(usageRecordService.createUsageRecord(eq(serviceId), any())).thenThrow(new IllegalArgumentException("not_found"));

        setAuthPrincipal("1", "ROLE_ADMIN");

        mvc.perform(post("/api/services/" + serviceId + "/usage")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isNotFound());

        verify(usageRecordService, times(1)).createUsageRecord(eq(serviceId), any());
    }
}
