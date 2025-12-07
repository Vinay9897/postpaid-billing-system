package com.abc.postpaid.customer.service;

import com.abc.postpaid.customer.dto.CustomerRequest;
import com.abc.postpaid.customer.entity.Customer;
import com.abc.postpaid.customer.repository.CustomerRepository;
import com.abc.postpaid.customer.service.impl.CustomerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.OffsetDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

public class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private com.abc.postpaid.user.repository.UserRepository userRepository;

    @InjectMocks
    private com.abc.postpaid.customer.service.impl.CustomerServiceImpl customerService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createCustomer_success() {
        CustomerRequest req = new CustomerRequest();
        req.setUserId(2L);
        req.setFullName("ACME Corp");
        req.setAddress("123 Road");

        com.abc.postpaid.user.entity.User u = new com.abc.postpaid.user.entity.User();
        u.setUserId(2L);
        u.setUsername("u");


        Customer saved = new Customer();
        saved.setCustomerId(11L);
        saved.setFullName("ACME Corp");
        saved.setPhoneNumber("+15551234567");

        when(userRepository.findById(2L)).thenReturn(java.util.Optional.of(u));
        when(customerRepository.save(any(Customer.class))).thenReturn(saved);

        Long id = customerService.createCustomer(req);
        assertThat(id).isEqualTo(11L);
    }
}
