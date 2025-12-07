package com.abc.postpaid.customer.controller;

import com.abc.postpaid.customer.dto.*;
import jakarta.validation.Valid;
import com.abc.postpaid.customer.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/customers")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCustomerController {

    @Autowired
    private CustomerService customerService;

    @PostMapping
    public ResponseEntity<?> createCustomer(@Valid @RequestBody CustomerRequest req) {
        Long id = customerService.createCustomer(req);
        return ResponseEntity.status(201).body(java.util.Map.of("customer_id", id));
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponse>> listCustomers() {
        return ResponseEntity.ok(customerService.listCustomers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getCustomer(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getCustomer(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCustomer(@PathVariable Long id, @Valid @RequestBody CustomerRequest req) {
        customerService.updateCustomer(id, req);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{customerId}/services")
    public ResponseEntity<?> createService(@PathVariable Long customerId, @Valid @RequestBody ServiceRequest req) {
        Long id = customerService.createServiceForCustomer(customerId, req);
        return ResponseEntity.status(201).body(java.util.Map.of("service_id", id));
    }

    @GetMapping("/{customerId}/services")
    public ResponseEntity<List<ServiceResponse>> listServices(@PathVariable Long customerId) {
        return ResponseEntity.ok(customerService.listServicesForCustomer(customerId));
    }
}
