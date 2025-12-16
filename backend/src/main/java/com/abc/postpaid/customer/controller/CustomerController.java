package com.abc.postpaid.customer.controller;

import com.abc.postpaid.customer.dto.CustomerRequest;
import com.abc.postpaid.customer.dto.CustomerResponse;
import com.abc.postpaid.customer.dto.OwnerCustomerUpdateRequest;
import com.abc.postpaid.customer.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.abc.postpaid.customer.dto.ServiceResponse;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    private Long getAuthUserId(Authentication auth) {
        if (auth == null || auth.getPrincipal() == null) return null;
        try {
            return Long.valueOf(String.valueOf(auth.getPrincipal()));
        } catch (Exception ex) {
            return null;
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getCustomer(@PathVariable Long id) {
        System.out.println("Debug: Entered getCustomer with id = " + id);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomerResponse resp = customerService.getCustomerByUserId(id);
        if (resp == null) {
            System.out.println("Debug: No customer found for user ID = " + id);
            return ResponseEntity.status(404).build();
        }
        Long authUserId = getAuthUserId(auth);
        System.out.println("Debug: Authenticated user ID = " + authUserId + ", Customer's user ID = " + resp.getUserId());
        if (authUserId != null && authUserId.equals(resp.getUserId())) {
            return ResponseEntity.ok(resp);
        }
        return ResponseEntity.status(403).build();
    }

   

    @GetMapping("/{id}/services")
    public ResponseEntity<List<ServiceResponse>> listServices(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long authUserId = getAuthUserId(auth);
        if (authUserId == null) return ResponseEntity.status(401).build();

        CustomerResponse existing = customerService.getCustomerByUserId(id);
        if (existing == null) return ResponseEntity.status(404).build();
        if (!authUserId.equals(existing.getUserId())) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(customerService.listServicesForCustomer(existing.getCustomerId()));
    }

}
