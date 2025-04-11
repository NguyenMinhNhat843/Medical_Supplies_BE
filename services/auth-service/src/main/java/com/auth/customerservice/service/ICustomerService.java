package com.auth.customerservice.service;

import com.auth.customerservice.dto.CustomerDTO;
import com.auth.customerservice.entity.CustomerEntity;
import com.auth.customerservice.model.CreateCustomerRequest;
import com.auth.customerservice.model.CustomerInfoResponse;
import com.auth.customerservice.model.UpdateCustomerRequest;

import java.util.List;
import java.util.Optional;

public interface ICustomerService {
    CustomerEntity saveCustomer(CustomerEntity customerEntity);
    Optional<CustomerInfoResponse> getCustomerByUserId(Long userId);

    List<CustomerEntity> getAllCustomer();
    void deleteCustomer(Long customerId);

    CustomerEntity updateCustomer(Long userId, UpdateCustomerRequest user);

    void createCustomerForUser(CreateCustomerRequest request);

    Optional<CustomerEntity> getCustomerByEmail(String email);


}
