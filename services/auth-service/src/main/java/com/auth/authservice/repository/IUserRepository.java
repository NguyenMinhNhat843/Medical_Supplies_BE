package com.auth.authservice.repository;

import com.auth.authservice.entity.UserEntity;
import com.auth.authservice.repository.custom.IUserRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface IUserRepository extends JpaRepository<UserEntity, Long>, IUserRepositoryCustom {

    Optional<UserEntity> findByUsername(String username);

    // Liệt kê tất cả các tài khoản có role là ADMIN và STAFF
    List<UserEntity> findByRoleIn(List<String> roles);
    // Liệt k kê tất cả các tài khoản có role là USER
    List<UserEntity> findByRole(String role);
}
