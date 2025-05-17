package com.customer.customerservice.service.inter;

import com.customer.customerservice.entity.Customer;
import com.customer.customerservice.repository.CustomerRepository;

import java.util.List;
import java.util.Optional;

public interface customers_interface {
    List<Customer> getAllCustomers() ;

    Optional<Customer> getCustomerById(Long id);

    Customer saveCustomer(Customer customer) ;

    boolean deleteCustomer(Long id) ;

    // Các phương thức tìm kiếm
    List<Customer> findByFirstname(String firstname);
    List<Customer> findByLastname(String lastname);
    List<Customer> findByEmail(String email);
    List<Customer> findByPhone(String phone);
}
