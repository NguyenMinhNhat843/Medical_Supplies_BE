package com.user.userservice.service.serviceImpl;

import com.user.userservice.converter.UserConverter;
import com.user.userservice.entity.UserEntity;
import com.user.userservice.exception.MyException;
import com.user.userservice.model.dto.UserDTO;
import com.user.userservice.repository.IUserRepository;
import com.user.userservice.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.Date;
import java.util.List;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private UserConverter userConverter;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Override
    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public UserDTO registerUser(UserDTO userDTO) throws MyException {
        if(userRepository.findOneByUsername(userDTO.getUsername())!=null){
            throw new MyException("Tên đăng nhập đã tồn tại");
        }
        UserEntity userEntity = userConverter.convertToEntity(userDTO);
        userEntity.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        userEntity.setRole("USER");
        return userConverter.convertToDto(userRepository.save(userEntity));
    }
}
