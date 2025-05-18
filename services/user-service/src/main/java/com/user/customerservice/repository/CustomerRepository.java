package com.user.customerservice.repository;

import com.user.customerservice.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity,Long> {

    Optional<CustomerEntity> findByUserId(Long userId);
    void deleteByUserId(Long userId);

    Optional<CustomerEntity> findByEmail(String email);

    @Query("SELECT c FROM CustomerEntity c WHERE " +
            "LOWER(CONCAT(c.lastName, ' ', c.firstName)) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR c.phone LIKE %:keyword%")
    List<CustomerEntity> searchByNameOrPhone(@Param("keyword") String keyword);
}
