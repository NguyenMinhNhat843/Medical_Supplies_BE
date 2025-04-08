package com.customer.customerservice.service.impl;

import com.customer.customerservice.entity.Customer;
import com.customer.customerservice.repository.CustomerRepository;
import com.customer.customerservice.service.inter.customers_interface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService implements customers_interface{
    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Optional<Customer> getCustomerById(Long id) {
        return customerRepository.findById(id);
    }

    public Customer saveCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    public boolean deleteCustomer(Long id) {
        if (customerRepository.existsById(id)) {
            System.out.println("Deleting customer with ID: " + id);
            customerRepository.deleteById(id);
            return true;
        }
        System.out.println("Customer not found with ID: " + id);
        return false;
    }

    // Các phương thức tìm kiếm
    @Override
    public List<Customer> findByFirstname(String firstname) {
        return customerRepository.findByFirstname(firstname);
    }

    @Override
    public List<Customer> findByLastname(String lastname) {
        return customerRepository.findByLastname(lastname);
    }

    @Override
    public List<Customer> findByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    @Override
    public List<Customer> findByPhone(String phone) {
        return customerRepository.findByPhone(phone);
    }

}

