package com.abc.postpaid.customer.repository;

import com.abc.postpaid.customer.entity.ServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<ServiceEntity, Long> {
    List<ServiceEntity> findByCustomerCustomerId(Long customerId);
}
