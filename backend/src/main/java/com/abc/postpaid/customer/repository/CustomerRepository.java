package com.abc.postpaid.customer.repository;

import com.abc.postpaid.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
