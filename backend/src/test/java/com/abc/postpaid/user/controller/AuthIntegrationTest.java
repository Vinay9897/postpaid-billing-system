package com.abc.postpaid.user.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

import com.abc.postpaid.customer.repository.CustomerRepository;
import com.abc.postpaid.customer.entity.Customer;
import com.abc.postpaid.user.repository.UserRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class AuthIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void registerAndLogin_endToEnd() {
        String base = "http://localhost:" + port;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String registerJson = "{\"username\":\"itest-user\",\"email\":\"itest@example.com\",\"password\":\"P@ssw0rd!\"}";
        ResponseEntity<Map> regResp = restTemplate.postForEntity(base + "/api/register", new HttpEntity<>(registerJson, headers), Map.class);

        assertThat(regResp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(regResp.getBody()).containsKey("user_id");

        String loginJson = "{\"username\":\"itest-user\",\"password\":\"P@ssw0rd!\"}";
        ResponseEntity<Map> loginResp = restTemplate.postForEntity(base + "/api/login", new HttpEntity<>(loginJson, headers), Map.class);

        assertThat(loginResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(loginResp.getBody()).containsKey("accessToken");
        assertThat(loginResp.getBody()).containsKey("expiresIn");

        String token = (String) loginResp.getBody().get("accessToken");
        assertThat(token).isNotBlank();
    }

    @Test
    void register_createsUserAndCustomer_linked() {
        String base = "http://localhost:" + port;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String registerJson = "{\"username\":\"itest-user2\",\"email\":\"itest2@example.com\",\"password\":\"P@ssw0rd!\",\"full_name\":\"Integration Test\",\"phone_number\":\"+15550001111\"}";
        ResponseEntity<Map> regResp = restTemplate.postForEntity(base + "/api/register", new HttpEntity<>(registerJson, headers), Map.class);

        assertThat(regResp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(regResp.getBody()).containsKey("user_id");

        Number userIdNum = (Number) regResp.getBody().get("user_id");
        Long userId = userIdNum.longValue();

        // verify user exists
        assertThat(userRepository.findById(userId)).isPresent();

        // verify customer exists and is linked to the created user
        boolean found = customerRepository.findAll().stream().anyMatch(c -> c.getUser() != null && c.getUser().getUserId().equals(userId));
        assertThat(found).isTrue();
    }
}
