package com.user.userservice.service.serviceImpl;

import com.user.userservice.entity.UserEntity;
import com.user.userservice.repository.IUserRepository;
import com.user.userservice.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private IUserRepository userRepository;
    @Override
    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }
}
