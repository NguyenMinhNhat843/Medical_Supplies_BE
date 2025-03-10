package com.user.userservice.service;

import com.user.userservice.entity.UserEntity;

import java.util.List;

public interface IUserService {
    List<UserEntity> getAllUsers();
}
