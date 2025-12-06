package com.abc.postpaid.user.controller;

import com.abc.postpaid.user.dto.AdminCreateUserRequest;
import com.abc.postpaid.user.dto.UserResponse;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import java.time.OffsetDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AdminUserControllerIntegrationTest {

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

    private String baseUrl(String path) {
        return "http://localhost:" + port + path;
    }

    @Test
    void listUsers_asAdmin_returnsUsers() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<UserResponse[]> resp = restTemplate.exchange(
                baseUrl("/api/admin/users"),
                HttpMethod.GET,
                entity,
                UserResponse[].class
        );

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody()).isNotNull();
        assertThat(resp.getBody()).extracting(UserResponse::getUsername).contains("admin");
    }

    @Test
    void createUser_asAdmin_createsAndReturnsId() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        AdminCreateUserRequest req = new AdminCreateUserRequest();
        req.setUsername("newuser");
        req.setEmail("new@example.com");
        req.setPassword("P@ssword1");
        req.setRole("customer");

        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(req), headers);

        ResponseEntity<Map> resp = restTemplate.postForEntity(baseUrl("/api/admin/users"), entity, Map.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(resp.getBody()).containsKey("user_id");
        Number id = (Number) resp.getBody().get("user_id");
        assertThat(id.longValue()).isPositive();

        // verify persisted
        User persisted = userRepository.findById(id.longValue()).orElseThrow();
        assertThat(persisted.getUsername()).isEqualTo("newuser");
    }

    
}
