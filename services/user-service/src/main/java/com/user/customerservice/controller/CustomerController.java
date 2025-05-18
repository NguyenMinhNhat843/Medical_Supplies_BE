package com.user.customerservice.controller;

import com.user.customerservice.dto.CustomerDTO;
import com.user.customerservice.entity.CustomerEntity;
import com.user.customerservice.model.*;
import com.user.customerservice.service.ICustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    public Optional<CustomerInfoResponse> getProfile(@RequestHeader("X-UserId") Long userId) {
//        Long userId = Long.parseLong(user.getName());
        System.out.println("✅ Received userId from header: " + userId);

        return customerService.getCustomerByUserId(userId);
    }

    // Cập nhật thông tin bản thân
//    @PostMapping("/me")
//    public CustomerEntity saveProfile(@RequestHeader("X-UserId") Long userId, @RequestBody CustomerEntity profile) {
//       //Long userId = Long.parseLong(user.getName());
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

//    @PutMapping("/add/{userId}")
//    public CustomerEntity updateCustomer(@PathVariable Long userId, @RequestBody UpdateCustomerRequest customer) {
//        return customerService.updateCustomer(userId, customer);
//    }

    // Tạo mới khách hàng
//    @PostMapping
//    public ResponseEntity<Void> createCustomerInfo(@RequestBody CreateCustomerRequest request) {
//        customerService.createCustomerForUser(request);
//        return ResponseEntity.ok().build();
//    }

    // Lấy thông tin khách hàng theo email
    @GetMapping("/email")
    public ResponseEntity<CustomerDTO> getCustomerByEmail(@RequestParam("value") String email) {
        return customerService.getCustomerByEmail(email)
                .map(entity -> {
                    CustomerDTO dto = new CustomerDTO();
                    dto.setUserId(entity.getUserId());
                    dto.setEmail(entity.getEmail());
                    return ResponseEntity.ok(dto);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    //  Cập nhật địa chỉ của khách hàng
    @PostMapping("/update/address")
    public ResponseEntity<String> updateAddress(@RequestHeader("X-UserId") Long userId, @RequestBody CreateAddressRequest request) {
        customerService.CreateOrUpdateCustomerAddess(userId, request);
        return ResponseEntity.ok("Cập nhật địa chỉ thành công!");
    }

    // Admin tạo tài khoản cho staff hoặc nhân viên
    @PostMapping("/add-staff")
    public ResponseEntity<?> addStaff(
            @RequestHeader("X-Role") String role,
            @RequestBody UserRegisterRequest request) {

        if (!"ADMIN".equalsIgnoreCase(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Bạn không có quyền thực hiện chức năng này.");
        }

        customerService.register(request);
        return ResponseEntity.ok("✅ Nhân viên đã được tạo thành công.");
    }

    // Lấy danh sách các nhân viên
    @GetMapping("/staffs")
    public ResponseEntity<List<UserFullInfoResponse>> getStaffAccounts() {
        List<UserFullInfoResponse> list = customerService.getStaffAccounts();
        return ResponseEntity.ok(list);
    }

    // Lấy danh sách các khách hàng
    @GetMapping("/customers")
    public ResponseEntity<List<UserFullInfoResponse>> getCustomerAccounts() {
        List<UserFullInfoResponse> list = customerService.getCustomerAccounts();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserFullInfoResponse>> searchUsers(@RequestParam String keyword) {
        return ResponseEntity.ok(customerService.searchCustomers(keyword));
    }

    // Tìm STAFF + ADMIN + Lọc theo role
    @GetMapping("/staffs/search")
    public ResponseEntity<List<UserFullInfoResponse>> searchStaffs(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "ALL") String role) {
        return ResponseEntity.ok(customerService.searchStaffByKeywordAndRole(keyword, role));
    }

    // Tìm USER (khách hàng)
    @GetMapping("/customers/search")
    public ResponseEntity<List<UserFullInfoResponse>> searchCustomers(
            @RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(customerService.searchByRoleAndKeyword("USER", keyword));
    }
}