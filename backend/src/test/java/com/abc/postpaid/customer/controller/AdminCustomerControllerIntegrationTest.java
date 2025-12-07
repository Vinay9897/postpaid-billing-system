package com.abc.postpaid.customer.controller;

import com.abc.postpaid.customer.dto.CustomerRequest;
import com.abc.postpaid.customer.dto.CustomerResponse;
import com.abc.postpaid.customer.dto.ServiceRequest;
import com.abc.postpaid.customer.dto.ServiceResponse;
import com.abc.postpaid.user.entity.User;
import com.abc.postpaid.user.repository.UserRepository;
import com.abc.postpaid.security.JwtProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.annotation.DirtiesContext;

import java.time.OffsetDateTime;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AdminCustomerControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtProvider jwtProvider;

    private String adminToken;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() throws Exception {
        userRepository.deleteAll();
        User admin = new User();
        admin.setUsername("admin");
        admin.setEmail("admin@example.com");
        admin.setPasswordHash(passwordEncoder.encode("adminpass"));
        admin.setRole("admin");
        admin.setCreatedAt(OffsetDateTime.now());
        User saved = userRepository.save(admin);
        adminToken = jwtProvider.generateToken(saved);
    }

    private String baseUrl(String path) { return "http://localhost:" + port + path; }

    @Test
    void create_and_list_customers_and_services_as_admin() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // create a customer owner user and create customer
        com.abc.postpaid.user.entity.User owner = new com.abc.postpaid.user.entity.User();
        owner.setUsername("owner");
        owner.setEmail("owner@example.com");
        owner.setPasswordHash(passwordEncoder.encode("ownerpass"));
        owner.setRole("user");
        owner.setCreatedAt(OffsetDateTime.now());
        com.abc.postpaid.user.entity.User ownerSaved = userRepository.save(owner);

        CustomerRequest creq = new CustomerRequest();
        creq.setUserId(ownerSaved.getUserId());
        creq.setFullName("Acme Ltd");
        creq.setAddress("123 Road");
        creq.setPhoneNumber("+15551234567");

        HttpEntity<String> cent = new HttpEntity<>(objectMapper.writeValueAsString(creq), headers);
        ResponseEntity<java.util.Map> cresp = restTemplate.postForEntity(baseUrl("/api/admin/customers"), cent, java.util.Map.class);
        assertThat(cresp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Number cid = (Number)cresp.getBody().get("customer_id");
        assertThat(cid.longValue()).isPositive();

        // list customers
        HttpEntity<Void> listEnt = new HttpEntity<>(headers);
        ResponseEntity<CustomerResponse[]> listResp = restTemplate.exchange(baseUrl("/api/admin/customers"), HttpMethod.GET, listEnt, CustomerResponse[].class);
        assertThat(listResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(listResp.getBody()).isNotEmpty();

        // create service
    ServiceRequest sreq = new ServiceRequest();
    sreq.setServiceType("STANDARD");
    sreq.setStatus("ACTIVE");

        HttpEntity<String> sent = new HttpEntity<>(objectMapper.writeValueAsString(sreq), headers);
        ResponseEntity<java.util.Map> sresp = restTemplate.postForEntity(baseUrl("/api/admin/customers/" + cid.longValue() + "/services"), sent, java.util.Map.class);
        assertThat(sresp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Number sid = (Number)sresp.getBody().get("service_id");
        assertThat(sid.longValue()).isPositive();

        // list services
        ResponseEntity<ServiceResponse[]> srvList = restTemplate.exchange(baseUrl("/api/admin/customers/" + cid.longValue() + "/services"), HttpMethod.GET, listEnt, ServiceResponse[].class);
        assertThat(srvList.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(srvList.getBody()).isNotEmpty();
    }
}
