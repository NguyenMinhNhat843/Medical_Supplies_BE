package com.user.userservice.service.serviceImpl;

import com.user.userservice.constant.SystemContant;
import com.user.userservice.converter.UserConverter;
import com.user.userservice.entity.UserEntity;
import com.user.userservice.exception.MyException;
import com.user.userservice.model.dto.PasswordDTO;
import com.user.userservice.model.dto.UserDTO;
import com.user.userservice.repository.IUserRepository;
import com.user.userservice.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


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

    @Override
    @Transactional
    public void updatePassword(long id, PasswordDTO passwordDTO) throws MyException {
        UserEntity userEntity = userRepository.findById(id).orElse(null);
        if(userEntity == null){
            throw new MyException("Người dùng không tồn tại trong ệ thống");
        }
        if (passwordEncoder.matches(passwordDTO.getOldPassword(),userEntity.getPassword()) &&
            passwordDTO.getNewPassword().equals(passwordDTO.getConfirmPassword())){
        userEntity.setPassword(passwordEncoder.encode(passwordDTO.getNewPassword()));
        userRepository.save(userEntity);

        }
        else {
            throw new MyException(SystemContant.CHANGE_PASSWORD_FAIL);
        }

    }
}
