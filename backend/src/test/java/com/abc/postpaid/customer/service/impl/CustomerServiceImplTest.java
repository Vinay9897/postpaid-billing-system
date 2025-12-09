package com.abc.postpaid.customer.service.impl;

import com.abc.postpaid.customer.dto.CustomerRequest;
import com.abc.postpaid.customer.dto.CustomerResponse;
import com.abc.postpaid.customer.dto.ServiceRequest;
import com.abc.postpaid.customer.dto.ServiceResponse;
import com.abc.postpaid.customer.entity.Customer;
import com.abc.postpaid.customer.entity.ServiceEntity;
import com.abc.postpaid.customer.repository.CustomerRepository;
import com.abc.postpaid.customer.repository.ServiceRepository;
import com.abc.postpaid.user.entity.User;
import com.abc.postpaid.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ServiceRepository serviceRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomerServiceImpl service;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
        reset(customerRepository, serviceRepository, userRepository);
    }

    @Test
    public void createCustomer_success() {
        CustomerRequest req = new CustomerRequest();
        req.setUserId(5L);
        req.setFullName("Tester");
        req.setAddress("Addr");
        req.setPhoneNumber("+1");

        User u = new User();
        u.setUserId(5L);
        when(userRepository.findById(5L)).thenReturn(Optional.of(u));

        Customer saved = new Customer();
        saved.setCustomerId(77L);
        when(customerRepository.save(any(Customer.class))).thenReturn(saved);

        Long result = service.createCustomer(req);
        assertEquals(77L, result);
        ArgumentCaptor<Customer> cap = ArgumentCaptor.forClass(Customer.class);
        verify(customerRepository).save(cap.capture());
        assertEquals("Tester", cap.getValue().getFullName());
    }

    @Test
    public void getCustomer_mapsFields() {
        Customer c = new Customer();
        c.setCustomerId(11L);
        User u = new User();
        u.setUserId(9L);
        c.setUser(u);
        c.setFullName("Name");
        c.setAddress("A");
        c.setPhoneNumber("P");

        when(customerRepository.findById(11L)).thenReturn(Optional.of(c));

        CustomerResponse r = service.getCustomer(11L);
        assertEquals(11L, r.getCustomerId());
        assertEquals(9L, r.getUserId());
        assertEquals("Name", r.getFullName());
    }

    @Test
    public void getCustomerByUserId_mapsFields() {
        Customer c = new Customer();
        c.setCustomerId(21L);
        User u = new User();
        u.setUserId(99L);
        c.setUser(u);
        c.setFullName("Uname");

        when(customerRepository.findByUserUserId(99L)).thenReturn(Optional.of(c));

        CustomerResponse r = service.getCustomerByUserId(99L);
        assertEquals(21L, r.getCustomerId());
        assertEquals(99L, r.getUserId());
        assertEquals("Uname", r.getFullName());
    }

    @Test
    public void listCustomers_returnsMappedList() {
        Customer c = new Customer();
        c.setCustomerId(3L);
        c.setFullName("L1");
        when(customerRepository.findAll()).thenReturn(List.of(c));

        var list = service.listCustomers();
        assertEquals(1, list.size());
        assertEquals("L1", list.get(0).getFullName());
    }

    @Test
    public void updateCustomer_updatesFields() {
        Customer existing = new Customer();
        existing.setCustomerId(7L);
        existing.setFullName("Old");
        when(customerRepository.findById(7L)).thenReturn(Optional.of(existing));

        CustomerRequest req = new CustomerRequest();
        req.setFullName("New");

        service.updateCustomer(7L, req);

        ArgumentCaptor<Customer> cap = ArgumentCaptor.forClass(Customer.class);
        verify(customerRepository).save(cap.capture());
        assertEquals("New", cap.getValue().getFullName());
    }

    @Test
    public void deleteCustomer_callsRepository() {
        service.deleteCustomer(8L);
        verify(customerRepository).deleteById(8L);
    }

    @Test
    public void createServiceForCustomer_createsService() {
        Customer c = new Customer();
        c.setCustomerId(2L);
        when(customerRepository.findById(2L)).thenReturn(Optional.of(c));

        ServiceEntity s = new ServiceEntity();
        s.setServiceId(55L);
        when(serviceRepository.save(any(ServiceEntity.class))).thenReturn(s);

        ServiceRequest req = new ServiceRequest();
        req.setServiceType("MOBILE");

        Long created = service.createServiceForCustomer(2L, req);
        assertEquals(55L, created);
        ArgumentCaptor<ServiceEntity> cap = ArgumentCaptor.forClass(ServiceEntity.class);
        verify(serviceRepository).save(cap.capture());
        assertEquals("MOBILE", cap.getValue().getServiceType());
    }

    @Test
    public void listServicesForCustomer_mapsServices() {
        ServiceEntity s = new ServiceEntity();
        s.setServiceId(101L);
        Customer c = new Customer();
        c.setCustomerId(201L);
        s.setCustomer(c);
        s.setServiceType("TV");
        s.setStartDate(OffsetDateTime.now());
        s.setStatus("ACTIVE");

        when(serviceRepository.findByCustomerCustomerId(201L)).thenReturn(List.of(s));

        var services = service.listServicesForCustomer(201L);
        assertEquals(1, services.size());
        ServiceResponse r = services.get(0);
        assertEquals(101L, r.getServiceId());
        assertEquals(201L, r.getCustomerId());
        assertEquals("TV", r.getServiceType());
    }
}
