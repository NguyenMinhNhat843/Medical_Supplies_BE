package com.auth.customerservice.controller;

import com.auth.customerservice.entity.CustomerEntity;
import com.auth.customerservice.service.ICustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private  ICustomerService customerService;

    @PostMapping("/create/{userId}")
    public ResponseEntity<?> createCustomer(@PathVariable Long userId, @RequestBody CustomerEntity customer) {
        return ResponseEntity.ok(customerService.createCustomer(customer, userId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getCustomerByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(customerService.getCustomerByUserId(userId));
    }
}