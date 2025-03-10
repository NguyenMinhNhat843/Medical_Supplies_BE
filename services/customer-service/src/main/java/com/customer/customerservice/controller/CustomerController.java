package com.customer.customerservice.controller;

import com.customer.customerservice.entity.Customer;
import com.customer.customerservice.service.impl.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/customers-list")
public class CustomerController {
    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public ResponseEntity<List<Customer>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long id) {
        return customerService.getCustomerById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer) {
        return ResponseEntity.status(HttpStatus.CREATED).body(customerService.saveCustomer(customer));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCustomer(@PathVariable Long id) {
        System.out.println("Received DELETE request for ID: " + id);

        boolean isDeleted = customerService.deleteCustomer(id);
        if (isDeleted) {
            return ResponseEntity.ok("Customer deleted successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer not found");
        }
    }

    // Các API tìm kiếm
    @GetMapping("/search/firstname/{firstname}")
    public ResponseEntity<List<Customer>> searchByFirstname(@PathVariable String firstname) {
        return ResponseEntity.ok(customerService.findByFirstname(firstname));
    }

    @GetMapping("/search/lastname/{lastname}")
    public ResponseEntity<List<Customer>> searchByLastname(@PathVariable String lastname) {
        return ResponseEntity.ok(customerService.findByLastname(lastname));
    }

    @GetMapping("/search/email/{email}")
    public ResponseEntity<List<Customer>> searchByEmail(@PathVariable String email) {
        return ResponseEntity.ok(customerService.findByEmail(email));
    }

    @GetMapping("/search/phone/{phone}")
    public ResponseEntity<List<Customer>> searchByPhone(@PathVariable String phone) {
        return ResponseEntity.ok(customerService.findByPhone(phone));
    }
}
