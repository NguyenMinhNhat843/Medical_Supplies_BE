package com.auth.customerservice.service;

import com.auth.customerservice.entity.CustomerEntity;

import java.util.Optional;

public interface ICustomerService {
    CustomerEntity saveCustomer(CustomerEntity customerEntity);
    Optional<CustomerEntity> getCustomerByUserId(Long userId);
}
