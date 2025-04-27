package com.user.customerservice.converter;

import com.user.customerservice.entity.CustomerEntity;
import com.user.customerservice.model.CreateAddressRequest;
import com.user.customerservice.model.CustomerInfoResponse;
import com.user.customerservice.model.UpdateCustomerRequest;
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


    public String extractFirstName(String fullName) {
        String[] parts = fullName.trim().split("\\s+");
        return parts.length > 1 ? parts[parts.length - 1] : fullName;
    }

    public String extractLastName(String fullName) {
        String[] parts = fullName.trim().split("\\s+");
        return parts.length > 1 ? String.join(" ", Arrays.copyOf(parts, parts.length - 1)) : "";
    }

    // Build Address
    public String buildAddress(CreateAddressRequest addressRequest){
        if (addressRequest == null) {
            return null;
        }
        StringBuilder address = new StringBuilder();
        if (addressRequest.getStreet() != null && !addressRequest.getStreet().isEmpty()) {
            if (!address.isEmpty()) {
                address.append(", ");
            }
            address.append(addressRequest.getStreet());
        }
        if (addressRequest.getDistrict() != null && !addressRequest.getDistrict().isEmpty()) {
            if (!address.isEmpty()) {
                address.append(", ");
            }
            address.append(addressRequest.getDistrict());
        }
        if (addressRequest.getCity() != null && !addressRequest.getCity().isEmpty()) {
            if (!address.isEmpty()) {
                address.append(", ");
            }
            address.append(addressRequest.getCity());
        }
        return address.toString();
    }
}
