package com.auth.customerservice.service;

import com.auth.customerservice.entity.CustomerEntity;

import java.util.List;
import java.util.Optional;

public interface ICustomerService {
    CustomerEntity saveCustomer(CustomerEntity customerEntity);
    Optional<CustomerEntity> getCustomerByUserId(Long userId);

    List<CustomerEntity> getAllCustomer();
    void deleteCustomer(Long customerId);

    CustomerEntity updateCustomer(Long userId, CustomerEntity user);
}
