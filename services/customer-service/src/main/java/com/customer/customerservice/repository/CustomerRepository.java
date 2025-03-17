package com.customer.customerservice.repository;

import com.customer.customerservice.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;



@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    List<Customer> findByFirstname(String firstname);
    List<Customer> findByLastname(String lastname);
    List<Customer> findByEmail(String email);
    List<Customer> findByPhone(String phone);
}
