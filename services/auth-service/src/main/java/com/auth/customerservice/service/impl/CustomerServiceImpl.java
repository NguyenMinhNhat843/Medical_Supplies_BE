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

import java.util.Optional;

@Service
public class CustomerServiceImpl implements ICustomerService {

    @Autowired
    private  CustomerRepository customerRepository;

    private  UserFeignClient userFeignClient;



    @Override
    public CustomerEntity createCustomer(CustomerEntity customerEntity, Long userId) {
        ResponseEntity<UserDTO> userResponse  = userFeignClient.getAccountById(customerEntity.getUserId(),"Bearer token");
        if(userResponse.getStatusCode() == HttpStatus.OK){
            customerEntity.setUserId(userId);
            return customerRepository.save(customerEntity);
        }
        else {
            throw new RuntimeException("User not found");
        }
    }

    @Override
    public Optional<CustomerEntity> getCustomerByUserId(Long userId) {
        return customerRepository.findByUserId(userId);
    }
}
