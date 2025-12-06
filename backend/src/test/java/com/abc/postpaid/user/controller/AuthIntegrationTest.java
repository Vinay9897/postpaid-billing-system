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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class AuthIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

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
}
