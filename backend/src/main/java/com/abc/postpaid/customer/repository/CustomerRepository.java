package com.abc.postpaid.customer.repository;

import com.abc.postpaid.customer.entity.Customer;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
	java.util.Optional<Customer> findByUserUserId(Long userId);
}
