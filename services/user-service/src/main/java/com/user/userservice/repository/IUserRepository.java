package com.user.userservice.repository;

import com.user.userservice.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


public interface IUserRepository extends JpaRepository<UserEntity, Long> {

    //List<UserEntity> getAllUsers();

}
