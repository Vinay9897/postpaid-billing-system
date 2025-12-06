package com.abc.postpaid.user.controller;

import com.abc.postpaid.user.dto.*;
import com.abc.postpaid.user.service.AdminUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    @Autowired
    private AdminUserService adminUserService;

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody AdminCreateUserRequest req) {
        Long id = adminUserService.createUser(req);
        return ResponseEntity.status(201).body(java.util.Map.of("user_id", id));
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> listUsers() {
        return ResponseEntity.ok(adminUserService.listUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(adminUserService.getUser(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody AdminUpdateUserRequest req) {
        adminUserService.updateUser(id, req);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/password")
    public ResponseEntity<?> setPassword(@PathVariable Long id, @RequestBody SetPasswordRequest req) {
        adminUserService.setPassword(id, req);
        return ResponseEntity.ok().build();
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        adminUserService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
