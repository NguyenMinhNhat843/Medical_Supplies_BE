package com.auth.authservice.service;

import com.auth.authservice.entity.UserEntity;
import com.auth.authservice.exception.MyException;
import com.auth.authservice.model.dto.PasswordDTO;
import com.auth.authservice.model.dto.UserDTO;
import com.auth.authservice.model.request.UserRegisterRequest;

import java.util.List;

public interface IUserService {
    List<UserEntity> getAllUsers();

    UserDTO registerUser(UserDTO UserDTO) throws MyException;

    // update password
    void updatePassword(long id, PasswordDTO passwordDTO) throws MyException;


    void updatePasswordOTP(Long userId, String newPassword) throws MyException;
    // login
    String login(String username, String password) throws Exception;

    void register(UserRegisterRequest userRegisterRequest) throws MyException;

}
