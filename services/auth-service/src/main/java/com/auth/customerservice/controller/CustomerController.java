package com.auth.customerservice.controller;

import com.auth.customerservice.dto.CustomerEmailDTO;
import com.auth.customerservice.entity.CustomerEntity;
import com.auth.customerservice.model.CreateCustomerRequest;
import com.auth.customerservice.model.UpdateCustomerRequest;
import com.auth.customerservice.service.ICustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/users")
public class CustomerController {

    @Autowired
    private  ICustomerService customerService;

    // Lấy thông tin khách hàng theo userId
    @GetMapping("/{userId}")
    public ResponseEntity<?> getCustomerByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(customerService.getCustomerByUserId(userId));
    }


    // Thông tin bản thân theo userId đăng nhập
    @GetMapping("/me")
    public Optional<CustomerEntity> getProfile(@RequestHeader("X-UserId") Long userId) {
//        Long userId = Long.parseLong(auth.getName());
        System.out.println("✅ Received userId from header: " + userId);

        return customerService.getCustomerByUserId(userId);
    }

    // Cập nhật thông tin bản thân
//    @PostMapping("/me")
//    public CustomerEntity saveProfile(@RequestHeader("X-UserId") Long userId, @RequestBody CustomerEntity profile) {
//       //Long userId = Long.parseLong(auth.getName());
//        profile.setUserId(userId);
//        return customerService.saveCustomer(profile);
//    }

    // Cập nhật thông tin cá nhân
    @PutMapping("/me")
    public ResponseEntity<String> updateCustomerInfo(
            @RequestHeader("X-UserId") Long userId,
            @RequestBody UpdateCustomerRequest request) {

        customerService.updateCustomer(userId, request);
        return ResponseEntity.ok("Cập nhật thông tin thành công!");
    }

    // CRUD Cho quản trị viên
    @GetMapping("/list")
    public ResponseEntity<?> getAllCustomer() {
        return ResponseEntity.ok(customerService.getAllCustomer());
    }

    @DeleteMapping("/delete/{customerId}")
    public void deleteCustomer(@PathVariable Long customerId) {
        customerService.deleteCustomer(customerId);
    }

    @PutMapping("/add/{userId}")
    public CustomerEntity updateCustomer(@PathVariable Long userId, @RequestBody UpdateCustomerRequest customer) {
        return customerService.updateCustomer(userId, customer);
    }

    // Tạo mới khách hàng
    @PostMapping
    public ResponseEntity<Void> createCustomerInfo(@RequestBody CreateCustomerRequest request) {
        customerService.createCustomerForUser(request.getUserId());
        return ResponseEntity.ok().build();
    }

    // Lấy thông tin khách hàng theo email
    @GetMapping("/email")
    public ResponseEntity<?> getCustomerByEmail(@RequestParam("value") String email) {
        return customerService.getCustomerByEmail(email)
                .map(customer -> ResponseEntity.ok(new CustomerEmailDTO(customer.getId(), customer.getEmail())))
                .orElse(ResponseEntity.notFound().build());
    }
}