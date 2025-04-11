package com.user.userservice.service.serviceImpl;

import com.user.userservice.constant.SystemContant;
import com.user.userservice.converter.UserConverter;
import com.user.userservice.entity.UserEntity;
import com.user.userservice.exception.MyException;
import com.user.userservice.model.dto.CreateCustomerRequest;
import com.user.userservice.model.dto.PasswordDTO;
import com.user.userservice.model.dto.UserDTO;
import com.user.userservice.model.request.UserRegisterRequest;
import com.user.userservice.repository.IUserRepository;
import com.user.userservice.service.IUserService;
import com.user.userservice.utils.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;


import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private UserConverter userConverter;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RestTemplate restTemplate;



    private final JwtTokenUtil jwtTokenUtil;

    private  AuthenticationManager authenticationManager;

    @Override
    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public UserDTO registerUser(UserDTO userDTO) throws MyException {
        if(userRepository.findByUsername(userDTO.getUsername()).isPresent()){
            throw new MyException("Tên đăng nhập đã tồn tại1");
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

    @Override
    public void updatePasswordOTP(Long userId, String newPassword) throws MyException {
        Optional<UserEntity> opt = userRepository.findById(userId);
        if (opt.isPresent()) {
            UserEntity user = opt.get();
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
        } else {
            throw new RuntimeException("UserId không tồn tại");
        }
    }

    @Override
    public String login(String username, String password) throws Exception {
        Optional<UserEntity> userEntity = userRepository.findByUsername(username);
        if(userEntity.isEmpty()){
            throw new MyException("Invalid phone number / password");
        }
        UserEntity existingUser = userEntity.get();
        // check pass
        if(!passwordEncoder.matches(password, existingUser.getPassword())){
            throw new MyException("Wrong phone number or password");
        }
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                username, password,
                existingUser.getAuthorities()
        );
        System.out.println("Stored password hash: " + existingUser.getPassword());
        System.out.println("Entered password: " + password);
        System.out.println("Match: " + passwordEncoder.matches(password, existingUser.getPassword()));

       // authenticationManager.authenticate(authenticationToken);
        return jwtTokenUtil.generateToken(existingUser);

    }

    @Override
    public void register(UserRegisterRequest userRegisterRequest) throws MyException {
        if(userRepository.findByUsername(userRegisterRequest.getUsername()).isPresent()){
            throw new MyException("Tên đăng nhập đã tồn tại");
        }
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(userRegisterRequest.getUsername());
        userEntity.setPassword(passwordEncoder.encode(userRegisterRequest.getPassword()));
        userEntity.setRole("USER");
        userRepository.save(userEntity);
        CreateCustomerRequest createCustomerRequest = new CreateCustomerRequest();
        createCustomerRequest.setUserId(userEntity.getId());
        createCustomerRequest.setEmail(userRegisterRequest.getEmail());

        try {
            restTemplate.postForObject("http://AUTH-SERVICE/users", createCustomerRequest, Void.class);
            System.out.println("✅ Đã gọi user-service tạo CustomerInfo cho userId: " + userEntity.getId());
        } catch (Exception e) {
            System.err.println("Gọi user-service thất bại: " + e.getMessage());
        }
    }
}
