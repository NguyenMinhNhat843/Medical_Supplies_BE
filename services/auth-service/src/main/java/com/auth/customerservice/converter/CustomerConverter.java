package com.auth.customerservice.converter;

import com.auth.customerservice.entity.CustomerEntity;
import com.auth.customerservice.model.CustomerInfoResponse;
import com.auth.customerservice.model.UpdateCustomerRequest;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class CustomerConverter {
    public CustomerInfoResponse toResponse(CustomerEntity customer) {
        CustomerInfoResponse response = new CustomerInfoResponse();
        response.setFullName(customer.getLastName() + " " + customer.getFirstName());
        response.setPhone(customer.getPhone());
        response.setEmail(customer.getEmail());
        response.setGender(customer.getGender());
        response.setDateOfBirth(customer.getDateOfBirth());
        response.setAddress(customer.getAddress());
        return response;
    }

    public CustomerEntity fromRequest(UpdateCustomerRequest request) {
        CustomerEntity entity = new CustomerEntity();
        entity.setFirstName(extractFirstName(request.getFullName()));
        entity.setLastName(extractLastName(request.getFullName()));
        entity.setPhone(request.getPhone());
        entity.setGender(request.getGender());
        entity.setDateOfBirth(request.getDateOfBirth());
        return entity;
    }


    private String extractFirstName(String fullName) {
        String[] parts = fullName.trim().split("\\s+");
        return parts.length > 1 ? parts[parts.length - 1] : fullName;
    }

    private String extractLastName(String fullName) {
        String[] parts = fullName.trim().split("\\s+");
        return parts.length > 1 ? String.join(" ", Arrays.copyOf(parts, parts.length - 1)) : "";
    }
}
