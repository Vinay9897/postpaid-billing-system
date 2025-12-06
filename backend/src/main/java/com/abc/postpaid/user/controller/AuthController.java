package com.abc.postpaid.user.controller;

import com.abc.postpaid.user.dto.AuthResponse;
import com.abc.postpaid.user.dto.LoginRequest;
import com.abc.postpaid.user.dto.RegisterRequest;
import com.abc.postpaid.user.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        Long userId = authService.register(request);
        return ResponseEntity.status(201).body(new java.util.HashMap<String, Object>() {{ put("user_id", userId); }});
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        AuthResponse resp = authService.login(request);
        return ResponseEntity.ok(resp);
    }
}
