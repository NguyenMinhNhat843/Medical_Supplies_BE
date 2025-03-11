package com.user.userservice.service;

import com.user.userservice.entity.UserEntity;
import com.user.userservice.exception.MyException;
import com.user.userservice.model.dto.PasswordDTO;
import com.user.userservice.model.dto.UserDTO;

import java.util.List;

public interface IUserService {
    List<UserEntity> getAllUsers();

    UserDTO registerUser(UserDTO UserDTO) throws MyException;

    // update password
    void updatePassword(long id, PasswordDTO passwordDTO) throws MyException;
}
