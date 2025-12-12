package com.abc.postpaid.billing.controller;

import com.abc.postpaid.billing.dto.InvoiceRequest;
import com.abc.postpaid.billing.dto.InvoiceResponse;
import com.abc.postpaid.billing.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

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

    @GetMapping("/{id}/invoices")
    public ResponseEntity<List<InvoiceResponse>> listInvoices(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long authUserId = getAuthUserId(auth);

        if (!isAdmin(auth)) {
            if (authUserId == null || !authUserId.equals(id)) {
                return ResponseEntity.status(403).build();
            }
        }

        List<InvoiceResponse> invoices = invoiceService.listInvoicesByCustomer(id);
        return ResponseEntity.ok(invoices);
    }

    @PostMapping("/{id}/invoices")
    public ResponseEntity<?> createInvoice(@PathVariable Long id, @RequestBody InvoiceRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!isAdmin(auth)) {
            return ResponseEntity.status(403).build();
        }

        try {
            Long invoiceId = invoiceService.createInvoice(id, request);
            return ResponseEntity.status(201).body("{\"invoiceId\":" + invoiceId + "}");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(404).build();
        }
    }
}
