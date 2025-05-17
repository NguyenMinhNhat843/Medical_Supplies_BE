package com.user.customerservice.repository;

import com.user.customerservice.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity,Long> {

    Optional<CustomerEntity> findByUserId(Long userId);
    void deleteByUserId(Long userId);

    Optional<CustomerEntity> findByEmail(String email);
}
