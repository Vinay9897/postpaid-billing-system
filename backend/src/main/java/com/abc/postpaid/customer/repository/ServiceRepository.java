package com.abc.postpaid.customer.repository;

import com.abc.postpaid.customer.entity.ServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceRepository extends JpaRepository<ServiceEntity, Long> {
    List<ServiceEntity> findByCustomerCustomerId(Long customerId);
}
