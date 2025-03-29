package com.auth.customerservice.controller;

import com.auth.customerservice.entity.CustomerEntity;
import com.auth.customerservice.service.ICustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private  ICustomerService customerService;


    @GetMapping("/{userId}")
    public ResponseEntity<?> getCustomerByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(customerService.getCustomerByUserId(userId));
    }

    @GetMapping("/me")
    public Optional<CustomerEntity> getProfile(Authentication auth) {
        Long userId = Long.parseLong(auth.getName());
        return customerService.getCustomerByUserId(userId);
    }

    @PostMapping("/me")
    public CustomerEntity saveProfile(Authentication auth, @RequestBody CustomerEntity profile) {
        Long userId = Long.parseLong(auth.getName());
        profile.setUserId(userId);
        return customerService.saveCustomer(profile);
    }
}