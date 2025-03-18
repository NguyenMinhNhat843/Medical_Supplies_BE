package com.auth.customerservice.service;

import com.auth.customerservice.entity.CustomerEntity;

import java.util.Optional;

public interface ICustomerService {
    CustomerEntity createCustomer(CustomerEntity customerEntity, Long userId);
    Optional<CustomerEntity> getCustomerByUserId(Long userId);
}
