package com.auth.customerservice.service.impl;

import com.auth.customerservice.dto.CustomerDTO;
import com.auth.customerservice.entity.CustomerEntity;
import com.auth.customerservice.model.UpdateCustomerRequest;
import com.auth.customerservice.repository.CustomerRepository;
import com.auth.customerservice.service.ICustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
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
    public CustomerEntity updateCustomer(Long userId, UpdateCustomerRequest customerUpdate) {
        CustomerEntity customer = customerRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Customer not found with userId: " + userId));

        customer.setFirstName(customerUpdate.getFirstName());
        customer.setLastName(customerUpdate.getLastName());
        customer.setPhone(customerUpdate.getPhone());
        customer.setAddress(customerUpdate.getAddress());
        customer.setEmail(customerUpdate.getEmail());

        return customerRepository.save(customer);
    }

    @Override
    public void createCustomerForUser(Long userId) {
        CustomerEntity customer = new CustomerEntity();
        customer.setUserId(userId);
        customer.setFirstName("");
        customer.setLastName("");
        customer.setPhone("");
        customer.setAddress("");
        customer.setEmail(null);
        customer.setCreatedAt(Date.from(LocalDateTime.now().atZone(java.time.ZoneId.systemDefault()).toInstant()));
        customerRepository.save(customer);
    }

    @Override
    public Optional<CustomerEntity> getCustomerByEmail(String email) {
        return customerRepository.findByEmail(email);
    }


}
