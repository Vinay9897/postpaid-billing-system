package com.abc.postpaid.user.service.impl;

import com.abc.postpaid.user.dto.AuthResponse;
import com.abc.postpaid.user.dto.LoginRequest;
import com.abc.postpaid.user.dto.RegisterRequest;
import com.abc.postpaid.user.entity.User;
import com.abc.postpaid.user.repository.UserRepository;
import com.abc.postpaid.user.service.AuthService;
import com.abc.postpaid.customer.entity.Customer;
import com.abc.postpaid.customer.repository.CustomerRepository;
import com.abc.postpaid.security.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    @Transactional
    public Long register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("username_exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("email_exists");
        }
    User user = new User();
    user.setUsername(request.getUsername());
    user.setEmail(request.getEmail());
    user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
    user.setRole("customer");
    user.setCreatedAt(OffsetDateTime.now());
        User saved = userRepository.save(user);
        // Create linked customer record (progressive profiling)
        Customer customer = new Customer();
        customer.setUser(saved);
        // Use provided profile fields if present
        customer.setFullName(request.getFullName() != null ? request.getFullName() : "");
        customer.setPhoneNumber(request.getPhoneNumber() != null ? request.getPhoneNumber() : "");
        customer.setAddress(null);
        customerRepository.save(customer);
        return saved.getUserId();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("invalid_credentials"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("invalid_credentials");
        }
        try {
            String token = jwtProvider.generateToken(user);
            return new AuthResponse(token, jwtProvider.getExpiresMinutes() * 60);
        } catch (Exception ex) {
            throw new RuntimeException("token_generation_failed", ex);
        }
    }
}
