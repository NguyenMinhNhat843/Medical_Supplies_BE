package com.auth.customerservice.service.impl;

import com.auth.customerservice.converter.CustomerConverter;
import com.auth.customerservice.dto.CustomerDTO;
import com.auth.customerservice.entity.CustomerEntity;
import com.auth.customerservice.model.CreateCustomerRequest;
import com.auth.customerservice.model.CustomerInfoResponse;
import com.auth.customerservice.model.UpdateCustomerRequest;
import com.auth.customerservice.repository.CustomerRepository;
import com.auth.customerservice.service.ICustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerServiceImpl implements ICustomerService {

    @Autowired
    private  CustomerRepository customerRepository;

    @Autowired
    private CustomerConverter customerConverter;

    private String extractFirstName(String fullName) {
        String[] parts = fullName.trim().split("\\s+");
        return parts.length > 1 ? parts[parts.length - 1] : fullName;
    }

    private String extractLastName(String fullName) {
        String[] parts = fullName.trim().split("\\s+");
        return parts.length > 1 ? String.join(" ", Arrays.copyOf(parts, parts.length - 1)) : "";
    }
    @Override
    public CustomerEntity saveCustomer(CustomerEntity customerEntity) {
        return customerRepository.save(customerEntity);
    }

    @Override
    public Optional<CustomerInfoResponse> getCustomerByUserId(Long userId) {
        CustomerEntity entity = customerRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));

        return Optional.ofNullable(customerConverter.toResponse(entity));
    }

    @Override
    public List<CustomerEntity> getAllCustomer() {
        return customerRepository.findAll();
    }

    @Override
    public void deleteCustomer(Long customerId) {
        customerRepository.deleteById(customerId);
    }

    @Override
    public CustomerEntity updateCustomer(Long userId, UpdateCustomerRequest customerUpdate) {
        CustomerEntity customer = customerRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Customer not found with userId: " + userId));

        customer.setFirstName(extractFirstName(customerUpdate.getFullName()));
        customer.setLastName(extractLastName(customerUpdate.getFullName()));

        customer.setPhone(customerUpdate.getPhone());
        customer.setAddress(customerUpdate.getAddress());
        customer.setEmail(customerUpdate.getEmail());
        customer.setGender(customerUpdate.getGender());
        customer.setDateOfBirth(customerUpdate.getDateOfBirth());

        return customerRepository.save(customer);
    }

    @Override
    public void createCustomerForUser(CreateCustomerRequest request) {
        CustomerEntity customer = new CustomerEntity();
        customer.setUserId(request.getUserId());
        customer.setFirstName("");
        customer.setLastName("");
        customer.setPhone("");
        customer.setAddress("");
        customer.setEmail(request.getEmail());
        customer.setGender("");
        customer.setDateOfBirth(null);
        customer.setCreatedAt(Date.from(LocalDateTime.now().atZone(java.time.ZoneId.systemDefault()).toInstant()));
        customerRepository.save(customer);
    }

    @Override
    public Optional<CustomerEntity> getCustomerByEmail(String email) {
        return customerRepository.findByEmail(email);
    }


}
