package com.auth.customerservice.service.impl;

import com.auth.customerservice.FeignClient.UserFeignClient;
import com.auth.customerservice.dto.UserDTO;
import com.auth.customerservice.entity.CustomerEntity;
import com.auth.customerservice.repository.CustomerRepository;
import com.auth.customerservice.service.ICustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerServiceImpl implements ICustomerService {

    @Autowired
    private  CustomerRepository customerRepository;



    @Override
    public CustomerEntity saveCustomer(CustomerEntity customerEntity) {
        return customerRepository.save(customerEntity);
    }

    @Override
    public Optional<CustomerEntity> getCustomerByUserId(Long userId) {
        return customerRepository.findByUserId(userId);
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
    public CustomerEntity updateCustomer(Long userId, CustomerEntity customerUpdate) {
        CustomerEntity customer = customerRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Customer not found with userId: " + userId));

        customer.setFirstName(customerUpdate.getFirstName());
        customer.setLastName(customerUpdate.getLastName());
        customer.setPhone(customerUpdate.getPhone());
        customer.setAddress(customerUpdate.getAddress());

        return customerRepository.save(customer);
    }
}
