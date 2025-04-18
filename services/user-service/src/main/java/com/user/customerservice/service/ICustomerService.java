package com.user.customerservice.service;

import com.user.customerservice.entity.CustomerEntity;
import com.user.customerservice.model.CreateCustomerRequest;
import com.user.customerservice.model.CustomerInfoResponse;
import com.user.customerservice.model.UpdateCustomerRequest;

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
