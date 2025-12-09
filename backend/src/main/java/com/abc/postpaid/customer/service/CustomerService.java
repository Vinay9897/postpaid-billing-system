package com.abc.postpaid.customer.service;

import com.abc.postpaid.customer.dto.*;

import java.util.List;

public interface CustomerService {
    Long createCustomer(CustomerRequest req);
    CustomerResponse getCustomer(Long id);
    CustomerResponse getCustomerByUserId(Long userId);
    List<CustomerResponse> listCustomers();
    void updateCustomer(Long id, CustomerRequest req);
    void deleteCustomer(Long id);

    Long createServiceForCustomer(Long customerId, ServiceRequest req);
    List<ServiceResponse> listServicesForCustomer(Long customerId);
}
