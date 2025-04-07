package com.auth.customerservice.service;

import com.auth.customerservice.dto.CustomerDTO;
import com.auth.customerservice.entity.CustomerEntity;
import com.auth.customerservice.model.UpdateCustomerRequest;

import java.util.List;
import java.util.Optional;

public interface ICustomerService {
    CustomerEntity saveCustomer(CustomerEntity customerEntity);
    Optional<CustomerEntity> getCustomerByUserId(Long userId);

    List<CustomerEntity> getAllCustomer();
    void deleteCustomer(Long customerId);

    CustomerEntity updateCustomer(Long userId, UpdateCustomerRequest user);

    void createCustomerForUser(Long userId);

    Optional<CustomerEntity> getCustomerByEmail(String email);

}
