package com.abc.postpaid.user.service.impl;

import com.abc.postpaid.user.dto.*;
import com.abc.postpaid.user.entity.User;
import com.abc.postpaid.user.repository.UserRepository;
import com.abc.postpaid.user.service.AdminUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminUserServiceImpl implements AdminUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public Long createUser(AdminCreateUserRequest req) {
        if (userRepository.existsByUsername(req.getUsername())) throw new IllegalArgumentException("username_exists");
        if (userRepository.existsByEmail(req.getEmail())) throw new IllegalArgumentException("email_exists");
        User u = new User();
        u.setUsername(req.getUsername());
        u.setEmail(req.getEmail());
        u.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        u.setRole(req.getRole() == null ? "customer" : req.getRole());
        u.setCreatedAt(OffsetDateTime.now());
        User saved = userRepository.save(u);
        return saved.getUserId();
    }

    @Override
    public UserResponse getUser(Long id) {
        User u = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("not_found"));
        return toResponse(u);
    }

    @Override
    public List<UserResponse> listUsers() {
        return userRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateUser(Long id, AdminUpdateUserRequest req) {
        User u = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("not_found"));
        if (req.getEmail() != null) u.setEmail(req.getEmail());
        if (req.getRole() != null) u.setRole(req.getRole());
        userRepository.save(u);
    }

    @Override
    @Transactional
    public void setPassword(Long id, SetPasswordRequest req) {
        User u = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("not_found"));
        u.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        userRepository.save(u);
    }


    @Override
    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    private UserResponse toResponse(User u) {
        UserResponse r = new UserResponse();
        r.setUserId(u.getUserId());
        r.setUsername(u.getUsername());
        r.setEmail(u.getEmail());
        r.setRole(u.getRole());
        r.setCreatedAt(u.getCreatedAt());
        return r;
    }
}
