package com.user.customerservice.service.impl;

import com.user.customerservice.converter.CustomerConverter;
import com.user.customerservice.entity.CustomerEntity;
import com.user.customerservice.model.CreateAddressRequest;
import com.user.customerservice.model.CreateCustomerRequest;
import com.user.customerservice.model.CustomerInfoResponse;
import com.user.customerservice.model.UpdateCustomerRequest;
import com.user.customerservice.repository.CustomerRepository;
import com.user.customerservice.service.ICustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerServiceImpl implements ICustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerConverter customerConverter;

    @Autowired
    private RestTemplate restTemplate;

    private String extractFirstName(String fullName) {
        String[] parts = fullName.trim().split("\\s+");
        return parts.length > 1 ? parts[parts.length - 1] : fullName;
    }

    private String extractLastName(String fullName) {
        String[] parts = fullName.trim().split("\\s+");
        return parts.length > 1 ? String.join(" ", Arrays.copyOf(parts, parts.length - 1)) : "";
    }

    private void sendUserUpdateToNotificationService(Long userId, String email) { // Loại bỏ deviceToken
        try {
            String url = "http://localhost:8081/api/users/update";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String body = String.format("{\"userId\":%d,\"email\":\"%s\"}", userId, email); // Chỉ gửi userId và email
            HttpEntity<String> request = new HttpEntity<>(body, headers);

            restTemplate.postForEntity(url, request, String.class);
            System.out.println("Sent user update to notification-service: userId=" + userId);
        } catch (Exception e) {
            System.err.println("Failed to send user update to notification-service: " + e.getMessage());
        }
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

        CustomerEntity updatedCustomer = customerRepository.save(customer);
        sendUserUpdateToNotificationService(userId, customerUpdate.getEmail()); // Loại bỏ deviceToken
        return updatedCustomer;
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
        sendUserUpdateToNotificationService(request.getUserId(), request.getEmail()); // Loại bỏ deviceToken
    }

    @Override
    public Optional<CustomerEntity> getCustomerByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    @Override
    public CustomerEntity CreateOrUpdateCustomerAddess(Long userId, CreateAddressRequest request) {
        CustomerEntity customer = customerRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Customer not found with userId: " + userId));

        String address = customerConverter.buildAddress(request);
        customer.setAddress(address);

        CustomerEntity updatedCustomer = customerRepository.save(customer);
        sendUserUpdateToNotificationService(userId, customer.getEmail()); // Loại bỏ deviceToken
        return updatedCustomer;
    }
}