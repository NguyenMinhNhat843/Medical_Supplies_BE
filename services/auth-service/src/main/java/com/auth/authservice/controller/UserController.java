package com.auth.authservice.controller;


import com.auth.authservice.client.CustomerClient;
import com.auth.authservice.entity.UserEntity;
import com.auth.authservice.exception.MyException;
import com.auth.authservice.model.dto.CustomerEmailDTO;
import com.auth.authservice.model.dto.PasswordDTO;
import com.auth.authservice.model.dto.UserDTO;
import com.auth.authservice.model.request.*;
import com.auth.authservice.model.response.AccountResponse;
import com.auth.authservice.repository.IUserRepository;
import com.auth.authservice.service.IUserService;
import com.auth.authservice.service.serviceImpl.EmailService;
import com.auth.authservice.service.serviceImpl.OtpService;
import com.auth.authservice.utils.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/auth")
@Slf4j

public class UserController {

    @Autowired
    private IUserService userService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private IUserRepository userRepository;

    @Autowired private CustomerClient customerClient;
    @Autowired private OtpService otpService;
    @Autowired private EmailService emailService;

    @GetMapping("/list")
    public List<UserEntity> getAllUsers() {
        return userService.getAllUsers();
    }

//    @PostMapping("/register")
//    public ResponseEntity<?> registerUser(@Valid @RequestBody UserDTO newUser, BindingResult bindingResult) {
//      if(bindingResult.hasErrors()){
//          List<String> errors = bindingResult.getFieldErrors()
//                  .stream()
//                  .map(FieldError::getDefaultMessage)
//                    .collect(Collectors.toList());
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
//      }
//        try {
//            return ResponseEntity.ok(userService.registerUser(newUser));
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
//        }
//
//    }
//    @PostMapping("/register")
//    public ResponseEntity<String> register(@RequestBody UserRegisterRequest request) throws MyException {
//        userService.register(request);
//        return ResponseEntity.ok("Đăng ký thành công!");
//    }

    @PostMapping("/register")
    public ResponseEntity<String> requestRegisterOtp(@RequestBody UserRegisterRequest request) {
        return userService.requestRegisterOtp(request);
    }

    @PostMapping("/register/confirm-otp")
    public ResponseEntity<String> confirmRegisterOtp(@RequestBody VerifyOtpRequest request) throws MyException {
        return userService.confirmRegisterOtp(request);
    }

    @PutMapping("/change-password/{id}")
    public ResponseEntity<?> changePasswordUser(@PathVariable Long id, @RequestBody PasswordDTO passwordDTO){
        try {
            userService.updatePassword(id, passwordDTO);
            return ResponseEntity.ok("Password updated successfully");
        } catch (MyException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDTO userDTO) {
      try {
          String token = userService.login(userDTO.getUsername(), userDTO.getPassword());
          return ResponseEntity.ok(Map.of("token", token));
      } catch (Exception e) {
          throw new RuntimeException(e);
      }
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(user -> ResponseEntity.ok(user))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/request-otp")
    public ResponseEntity<String> requestOtp(@RequestBody RequestOtpRequest request) {
        Optional<CustomerEmailDTO> userOpt = customerClient.getCustomerByEmail(request.getEmail());
        if (userOpt.isEmpty()) return ResponseEntity.badRequest().body("Email không tồn tại");

        CustomerEmailDTO user = userOpt.get();
        String otp = otpService.generateOtp(request.getEmail(), user.getUserId());
        emailService.sendOtp(request.getEmail(), otp);
        return ResponseEntity.ok("OTP đã được gửi tới email.");
    }


    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestBody VerifyOtpRequest request) {
        boolean valid = otpService.validateOtp(request.getEmail(), request.getOtp());
        if (!valid) return ResponseEntity.badRequest().body("OTP không đúng hoặc đã hết hạn");
        return ResponseEntity.ok("OTP hợp lệ. Bạn có thể đặt lại mật khẩu.");
    }


    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) throws MyException {

        Long userId = otpService.getUserIdFromOtpStore(request.getEmail());

        if (userId == null) return ResponseEntity.badRequest().body("Không tìm thấy userId từ email");

        userService.updatePasswordOTP(userId, request.getNewPassword()); // ✅ gọi auth-service nội bộ
        otpService.clearOtp(request.getEmail());
        return ResponseEntity.ok("Đặt lại mật khẩu thành công");
    }


    // Kiểm tra tên đăng nhập đã tồn tại hay chưa khi tạo từ user-service qua
    @GetMapping("/accounts/check-username")
    public ResponseEntity<Boolean> checkUsername(@RequestParam String username) {
        boolean exists = userRepository.findByUsername(username).isPresent();
        return ResponseEntity.ok(exists);
    }

    // Admin Tao tài khoản đụược gọi từ user-service
    @PostMapping("/accounts")
    public ResponseEntity<Long> createAccount(@RequestBody CreateAccountRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null); // đã tồn tại
        }

        UserEntity account = new UserEntity();
        account.setUsername(request.getUsername());
        account.setPassword(passwordEncoder.encode(request.getPassword()));
        account.setRole(request.getRole());
        account.setCreateAt(Date.from(LocalDateTime.now().atZone(java.time.ZoneId.systemDefault()).toInstant()));
        account.setUpdateAt(Date.from(LocalDateTime.now().atZone(java.time.ZoneId.systemDefault()).toInstant()));

        userRepository.save(account);
        return ResponseEntity.ok(account.getId());
    }

    // Get List tài khoản nhân viên
    @GetMapping("/accounts/staffs")
    public ResponseEntity<List<AccountResponse>> getStaffs() {
        List<AccountResponse> result = userService.getStaffAccounts();
        return ResponseEntity.ok(result);
    }

    // Get List tài khoản khách hàng
    @GetMapping("/accounts/users")
    public ResponseEntity<List<AccountResponse>> getUsers() {
        return ResponseEntity.ok(userService.getUsers());
    }

    // Search tài khoản
    @GetMapping("/accounts/{userId}")
    public ResponseEntity<AccountResponse> getAccountById(@PathVariable Long userId) {
        try {
            AccountResponse account = userService.getAccountById(userId);
            return ResponseEntity.ok(account);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }


    // Cập nhật tài khoản


}
