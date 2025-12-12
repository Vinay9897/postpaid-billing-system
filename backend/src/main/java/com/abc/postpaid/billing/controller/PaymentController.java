package com.abc.postpaid.billing.controller;

import com.abc.postpaid.billing.dto.PaymentRequest;
import com.abc.postpaid.billing.dto.PaymentResponse;
import com.abc.postpaid.billing.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/api/invoices")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    private boolean isAdmin(Authentication auth) {
        if (auth == null) return false;
        Collection<? extends GrantedAuthority> auths = auth.getAuthorities();
        if (auths == null) return false;
        return auths.stream().anyMatch(a -> String.valueOf(a.getAuthority()).equalsIgnoreCase("ROLE_ADMIN"));
    }

    private Long getAuthUserId(Authentication auth) {
        if (auth == null || auth.getPrincipal() == null) return null;
        try {
            return Long.valueOf(String.valueOf(auth.getPrincipal()));
        } catch (Exception ex) {
            return null;
        }
    }

    @PostMapping("/{id}/payments")
    public ResponseEntity<?> recordPayment(@PathVariable Long id, @RequestBody PaymentRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return ResponseEntity.status(401).build();
        }

        try {
            Long paymentId = paymentService.recordPayment(id, request);
            return ResponseEntity.status(201).body("{\"paymentId\":" + paymentId + "}");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(404).build();
        }
    }

    @GetMapping("/{id}/payments")
    public ResponseEntity<List<PaymentResponse>> listPayments(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return ResponseEntity.status(401).build();
        }

        List<PaymentResponse> payments = paymentService.listPaymentsByInvoice(id);
        return ResponseEntity.ok(payments);
    }
}
