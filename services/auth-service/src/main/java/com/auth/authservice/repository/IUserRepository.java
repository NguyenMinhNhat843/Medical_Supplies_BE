package com.auth.authservice.repository;

import com.auth.authservice.entity.UserEntity;
import com.auth.authservice.repository.custom.IUserRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface IUserRepository extends JpaRepository<UserEntity, Long>, IUserRepositoryCustom {

    Optional<UserEntity> findByUsername(String username);

}
