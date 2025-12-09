package com.abc.postpaid.customer.service.impl;

import com.abc.postpaid.customer.dto.*;
import com.abc.postpaid.customer.entity.Customer;
import com.abc.postpaid.user.entity.User;
import com.abc.postpaid.user.repository.UserRepository;
import com.abc.postpaid.customer.entity.ServiceEntity;
import com.abc.postpaid.customer.repository.CustomerRepository;
import com.abc.postpaid.customer.repository.ServiceRepository;
import com.abc.postpaid.customer.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public Long createCustomer(CustomerRequest req) {
        User u = userRepository.findById(req.getUserId()).orElseThrow(() -> new IllegalArgumentException("user_not_found"));
        Customer c = new Customer();
        c.setUser(u);
        c.setFullName(req.getFullName());
        c.setAddress(req.getAddress());
        c.setPhoneNumber(req.getPhoneNumber());
        Customer saved = customerRepository.save(c);
        return saved.getCustomerId();
    }

    @Override
    public CustomerResponse getCustomer(Long id) {
        Customer c = customerRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("not_found"));
        CustomerResponse r = new CustomerResponse();
        r.setCustomerId(c.getCustomerId());
        r.setUserId(c.getUser() != null ? c.getUser().getUserId() : null);
        r.setFullName(c.getFullName());
        r.setAddress(c.getAddress());
        r.setPhoneNumber(c.getPhoneNumber());
        return r;
    }

    @Override
    public CustomerResponse getCustomerByUserId(Long userId){
        Customer c = customerRepository.findByUserUserId(userId).orElseThrow(() -> new IllegalArgumentException("not_found"));
        CustomerResponse r = new CustomerResponse();
        r.setCustomerId(c.getCustomerId());
        r.setUserId(c.getUser() != null ? c.getUser().getUserId() : null);
        r.setFullName(c.getFullName());
        r.setAddress(c.getAddress());
        r.setPhoneNumber(c.getPhoneNumber());
        return r;
    }

    @Override
    public List<CustomerResponse> listCustomers() {
        return customerRepository.findAll().stream().map(c -> {
            CustomerResponse r = new CustomerResponse();
            r.setCustomerId(c.getCustomerId());
            r.setUserId(c.getUser() != null ? c.getUser().getUserId() : null);
            r.setFullName(c.getFullName());
            r.setAddress(c.getAddress());
            r.setPhoneNumber(c.getPhoneNumber());
            return r;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateCustomer(Long id, CustomerRequest req) {
        Customer c = customerRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("not_found"));
        if (req.getFullName() != null) c.setFullName(req.getFullName());
        if (req.getAddress() != null) c.setAddress(req.getAddress());
        if (req.getPhoneNumber() != null) c.setPhoneNumber(req.getPhoneNumber());
        customerRepository.save(c);
    }

    @Override
    @Transactional
    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Long createServiceForCustomer(Long customerId, ServiceRequest req) {
        Customer c = customerRepository.findById(customerId).orElseThrow(() -> new IllegalArgumentException("not_found"));
        ServiceEntity s = new ServiceEntity();
        s.setCustomer(c);
        s.setServiceType(req.getServiceType());
        s.setStartDate(req.getStartDate() != null ? req.getStartDate() : OffsetDateTime.now());
        s.setStatus(req.getStatus() != null ? req.getStatus() : "ACTIVE");
        ServiceEntity saved = serviceRepository.save(s);
        return saved.getServiceId();
    }

    @Override
    public List<ServiceResponse> listServicesForCustomer(Long customerId) {
        return serviceRepository.findByCustomerCustomerId(customerId).stream().map(s -> {
            ServiceResponse r = new ServiceResponse();
            r.setServiceId(s.getServiceId());
            r.setCustomerId(s.getCustomer().getCustomerId());
            r.setServiceType(s.getServiceType());
            r.setStartDate(s.getStartDate());
            r.setStatus(s.getStatus());
            return r;
        }).collect(Collectors.toList());
    }
}
