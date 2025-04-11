package com.user.userservice.controller;


import com.user.userservice.client.CustomerClient;
import com.user.userservice.entity.UserEntity;
import com.user.userservice.exception.MyException;
import com.user.userservice.model.dto.CustomerEmailDTO;
import com.user.userservice.model.dto.PasswordDTO;
import com.user.userservice.model.dto.UserDTO;
import com.user.userservice.model.request.RequestOtpRequest;
import com.user.userservice.model.request.ResetPasswordRequest;
import com.user.userservice.model.request.UserRegisterRequest;
import com.user.userservice.model.request.VerifyOtpRequest;
import com.user.userservice.repository.IUserRepository;
import com.user.userservice.service.IUserService;
import com.user.userservice.service.serviceImpl.EmailService;
import com.user.userservice.service.serviceImpl.OtpService;
import com.user.userservice.utils.JwtTokenUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
@Slf4j

public class UserController {

    @Autowired
    private IUserService userService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;


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
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserRegisterRequest request) throws MyException {
        userService.register(request);
        return ResponseEntity.ok("Đăng ký thành công!");
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

}
