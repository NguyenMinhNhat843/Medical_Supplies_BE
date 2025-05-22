package com.auth.authservice.service.serviceImpl;

import com.auth.authservice.constant.SystemContant;
import com.auth.authservice.converter.UserConverter;
import com.auth.authservice.entity.UserEntity;
import com.auth.authservice.exception.MyException;
import com.auth.authservice.model.dto.CreateCustomerRequest;
import com.auth.authservice.model.dto.PasswordDTO;
import com.auth.authservice.model.dto.UserDTO;
import com.auth.authservice.model.request.UserRegisterRequest;
import com.auth.authservice.model.request.VerifyOtpRequest;
import com.auth.authservice.model.response.AccountResponse;
import com.auth.authservice.repository.IUserRepository;
import com.auth.authservice.service.IUserService;
import com.auth.authservice.utils.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;


import javax.swing.text.StyledEditorKit;
import java.util.List;
import java.util.NoSuchElementException;
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
    @Autowired
    private OtpService otpService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private EmailVerificationService emailVerificationService;


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
        System.out.println("UserEntity: " + userEntity);
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
    public Long register(UserRegisterRequest userRegisterRequest) throws MyException {
        String checkUrl = "http://USER-SERVICE/users/check-email?email=" + userRegisterRequest.getEmail();
        Boolean exist = restTemplate.getForObject(checkUrl, Boolean.class);
        if(userRepository.findByUsername(userRegisterRequest.getUsername()).isPresent()){
            throw new MyException("Tên đăng nhập đã tồn tại!!! Vui lòng kiểm tra lại");
        } else if ( Boolean.TRUE.equals(exist)) {
            throw new MyException("Email đã tồn tại!!! Vui lòng kiểm tra lại");
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
            restTemplate.postForObject("http://USER-SERVICE/users", createCustomerRequest, Void.class);
            System.out.println("Đã gọi auth-service tạo CustomerInfo cho userId: " + userEntity.getId());
        } catch (Exception e) {
            System.err.println("Gọi auth-service thất bại: " + e.getMessage());
        }
        return userEntity.getId();
    }

    // Lấy danh sách tài khoản nhân viên và admin
    @Override
    public List<AccountResponse> getStaffAccounts() {
        List<UserEntity> list = userRepository.findByRoleIn(List.of("ADMIN", "STAFF"));
        return list.stream()
                .map(acc -> new AccountResponse(
                        acc.getId(),
                        acc.getUsername(),
                        acc.getRole()
                ))
                .toList();
    }

    // Lấy thông tin tài khoản theo role USER
    @Override
    public List<AccountResponse> getUsers() {
        List<UserEntity> list = userRepository.findByRole("USER");
        return list.stream()
                .map(acc -> new AccountResponse(
                        acc.getId(),
                        acc.getUsername(),
                        acc.getRole()
                ))
                .toList();
    }

    @Override
    public AccountResponse getAccountById(Long userId) {
        return userRepository.findById(userId)
                .map(acc -> new AccountResponse(
                        acc.getId(),
                        acc.getUsername(),
                        acc.getRole()))
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy tài khoản với ID: " + userId));
    }


    @Override
    public ResponseEntity<String> requestRegisterOtp(UserRegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Tên đăng nhập đã tồn tại.");
        }

        otpService.storeRegistrationInfo(request.getEmail(), request);
        String otp = otpService.generateOtp(request.getEmail(), null);
        emailService.sendRegisterOtp(request.getEmail(), otp);

        return ResponseEntity.ok("Đã gửi mã OTP đến email. Vui lòng kiểm tra hộp thư.");
    }

    @Override
    public ResponseEntity<String> confirmRegisterOtp(VerifyOtpRequest request) throws MyException {
        boolean valid = otpService.validateOtp(request.getEmail(), request.getOtp());
        if (!valid) {
            return ResponseEntity.badRequest().body("OTP không đúng hoặc đã hết hạn.");
        }

        UserRegisterRequest pending = otpService.getRegistrationInfo(request.getEmail());
        if (pending == null) {
            return ResponseEntity.badRequest().body("Không tìm thấy thông tin đăng ký.");
        }

        // Gọi lại register
        this.register(pending);

        otpService.clearOtp(request.getEmail());
        return ResponseEntity.ok("Đăng ký thành công!");
    }


}
