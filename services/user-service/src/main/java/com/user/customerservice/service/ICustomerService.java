package com.user.customerservice.service;

import com.user.customerservice.entity.CustomerEntity;
import com.user.customerservice.model.*;

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

    CustomerEntity CreateOrUpdateCustomerAddess(Long userId, CreateAddressRequest request);

    void register(UserRegisterRequest request);

    List<UserFullInfoResponse> getStaffAccounts();

    List<UserFullInfoResponse> getCustomerAccounts();

    List<UserFullInfoResponse> searchCustomers(String keyword);

    List<UserFullInfoResponse> searchByRoleAndKeyword(String roleGroup, String keyword);
    List<UserFullInfoResponse> searchStaffByKeywordAndRole(String keyword, String roleFilter); // Cho STAFF + ADMIN

}
