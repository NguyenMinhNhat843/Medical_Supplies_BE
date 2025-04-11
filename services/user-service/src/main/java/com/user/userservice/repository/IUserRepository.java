package com.user.userservice.repository;

import com.user.userservice.entity.UserEntity;
import com.user.userservice.repository.custom.IUserRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface IUserRepository extends JpaRepository<UserEntity, Long>, IUserRepositoryCustom {

    Optional<UserEntity> findByUsername(String username);

}
