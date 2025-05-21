package com.user.customerservice.service.impl;

import com.user.customerservice.converter.CustomerConverter;
import com.user.customerservice.entity.CustomerEntity;
import com.user.customerservice.model.*;
import com.user.customerservice.repository.CustomerRepository;
import com.user.customerservice.service.ICustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements ICustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerConverter customerConverter;
    @Autowired
    private RestTemplate restTemplate;


    @Value("${auth-service.url}")
    private String authServiceUrl;



    private String extractFirstName(String fullName) {
        String[] parts = fullName.trim().split("\\s+");
        return parts.length > 1 ? parts[parts.length - 1] : fullName;
    }

    private String extractLastName(String fullName) {
        String[] parts = fullName.trim().split("\\s+");
        return parts.length > 1 ? String.join(" ", Arrays.copyOf(parts, parts.length - 1)) : "";
    }

    private void sendUserUpdateToNotificationService(Long userId, String email) { // Loại bỏ deviceToken
        try {
            String url = "http://localhost:8081/api/users/update";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String body = String.format("{\"userId\":%d,\"email\":\"%s\"}", userId, email); // Chỉ gửi userId và email
            HttpEntity<String> request = new HttpEntity<>(body, headers);

            restTemplate.postForEntity(url, request, String.class);
            System.out.println("Sent user update to notification-service: userId=" + userId);
        } catch (Exception e) {
            System.err.println("Failed to send user update to notification-service: " + e.getMessage());
        }
    }

    @Override
    public CustomerEntity saveCustomer(CustomerEntity customerEntity) {
        return customerRepository.save(customerEntity);
    }

    @Override
    public Optional<CustomerInfoResponse> getCustomerByUserId(Long userId) {
        CustomerEntity entity = customerRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));
        return Optional.ofNullable(customerConverter.toResponse(entity));
    }

    @Override
    public List<CustomerEntity> getAllCustomer() {
        return customerRepository.findAll();
    }

    @Override
    public void deleteCustomer(Long customerId) {
        customerRepository.deleteById(customerId);
    }

    @Override
    public CustomerEntity updateCustomer(Long userId, UpdateCustomerRequest customerUpdate) {
        CustomerEntity customer = customerRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Customer not found with userId: " + userId));

        customer.setFirstName(extractFirstName(customerUpdate.getFullName()));
        customer.setLastName(extractLastName(customerUpdate.getFullName()));
        customer.setPhone(customerUpdate.getPhone());
        customer.setAddress(customerUpdate.getAddress());
//        customer.setEmail(customerUpdate.getEmail());
        customer.setGender(customerUpdate.getGender());
        customer.setDateOfBirth(customerUpdate.getDateOfBirth());

        CustomerEntity updatedCustomer = customerRepository.save(customer);
        //sendUserUpdateToNotificationService(userId, customerUpdate.getEmail()); // Loại bỏ deviceToken
        return updatedCustomer;
    }

    @Override
    public void createCustomerForUser(CreateCustomerRequest request) {
        CustomerEntity customer = new CustomerEntity();
        customer.setUserId(request.getUserId());
        customer.setFirstName("");
        customer.setLastName("");
        customer.setPhone("");
        customer.setAddress("");
        customer.setEmail(request.getEmail());
        customer.setGender("");
        customer.setDateOfBirth(null);
        customer.setCreatedAt(Date.from(LocalDateTime.now().atZone(java.time.ZoneId.systemDefault()).toInstant()));

        customerRepository.save(customer);
        sendUserUpdateToNotificationService(request.getUserId(), request.getEmail()); // Loại bỏ deviceToken
    }

    @Override
    public Optional<CustomerEntity> getCustomerByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    @Override
    public CustomerEntity CreateOrUpdateCustomerAddess(Long userId, CreateAddressRequest request) {
        CustomerEntity customer = customerRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Customer not found with userId: " + userId));

        String address = customerConverter.buildAddress(request);
        customer.setAddress(address);

        CustomerEntity updatedCustomer = customerRepository.save(customer);
        //sendUserUpdateToNotificationService(userId, customer.getEmail()); // Loại bỏ deviceToken
        return updatedCustomer;
    }


    @Override
    public void register(UserRegisterRequest request) {
        String baseUsername = request.getEmail().split("@")[0];
        String finalUsername = baseUsername;
        // Kiểm tra username đã tồn tại chưa
        int suffix = 1;
        while (checkUsernameExists(finalUsername)) {
            finalUsername = baseUsername + "_" + suffix++;
        }

        // Gọi auth-service để tạo account
        CreateAccountRequest createAccountReq = new CreateAccountRequest(
                finalUsername,
                request.getPassword(),
                request.getRole().toUpperCase()
        );
        // Lấy header để gửi request
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CreateAccountRequest> entity = new HttpEntity<>(createAccountReq, headers);

        ResponseEntity<Long> response = restTemplate.postForEntity(
                authServiceUrl + "/auth/accounts",
                entity,
                Long.class
        );

        Long accountId = response.getBody();

        // Tách tên
        String[] parts = request.getFullName().trim().split(" ");
        String firstName = parts[parts.length - 1];
        String lastName = String.join(" ", Arrays.copyOf(parts, parts.length - 1));

        // Lưu vào CustomerInfo
        CustomerEntity info = new CustomerEntity();
        info.setFirstName(firstName);
        info.setLastName(lastName);
        info.setEmail(request.getEmail());
        info.setPhone(request.getPhone());
        info.setAddress(request.getAddress());
        info.setUserId(accountId);
        info.setCreatedAt(Date.from(LocalDateTime.now().atZone(java.time.ZoneId.systemDefault()).toInstant()));
        info.setUpdatedAt(Date.from(LocalDateTime.now().atZone(java.time.ZoneId.systemDefault()).toInstant()));

        customerRepository.save(info);

        System.out.println("Đã tạo nhân viên và có username là: " + finalUsername);
    }

    @Override
    public List<UserFullInfoResponse> getStaffAccounts() {
        ResponseEntity<AccountResponse[]> response = restTemplate.getForEntity(
                authServiceUrl + "/auth/accounts/staffs",
                AccountResponse[].class
        );

        AccountResponse[] accounts = response.getBody();
        if (accounts == null) return Collections.emptyList();

        List<UserFullInfoResponse> result = new ArrayList<>();

        for (AccountResponse acc : accounts) {
            Optional<CustomerEntity> optional = customerRepository.findByUserId(acc.getId());

            String fullName = null, email = null, phone = null, address = null;

            if (optional.isPresent()) {
                CustomerEntity c = optional.get();
                fullName = c.getLastName() + " " + c.getFirstName();
                email = c.getEmail();
                phone = c.getPhone();
                address = c.getAddress();
            }

            result.add(new UserFullInfoResponse(
                    acc.getId(),
                    acc.getUsername(),
                    acc.getRole(),
                    fullName,
                    email,
                    phone,
                    address
            ));
        }

        return result;
    }

    @Override
    public List<UserFullInfoResponse> getCustomerAccounts() {
        ResponseEntity<AccountResponse[]> response = restTemplate.getForEntity(
                authServiceUrl + "/auth/accounts/users",
                AccountResponse[].class
        );

        AccountResponse[] accounts = response.getBody();
        if (accounts == null) return Collections.emptyList();

        List<UserFullInfoResponse> result = new ArrayList<>();

        for (AccountResponse acc : accounts) {
            Optional<CustomerEntity> optional = customerRepository.findByUserId(acc.getId());

            String fullName = null, email = null, phone = null, address = null;

            if (optional.isPresent()) {
                CustomerEntity c = optional.get();
                fullName = c.getLastName() + " " + c.getFirstName();
                email = c.getEmail();
                phone = c.getPhone();
                address = c.getAddress();
            }

            result.add(new UserFullInfoResponse(
                    acc.getId(),
                    acc.getUsername(),
                    acc.getRole(),
                    fullName,
                    email,
                    phone,
                    address
            ));
        }

        return result;
    }

    @Override
    public List<UserFullInfoResponse> searchCustomers(String keyword) {
        List<CustomerEntity> customers = customerRepository.searchByNameOrPhone(keyword);

        List<UserFullInfoResponse> result = new ArrayList<>();

        for (CustomerEntity c : customers) {
            // Gọi auth-service để lấy account theo userId
            String url = authServiceUrl + "/auth/accounts/" + c.getUserId();
            try {
                ResponseEntity<AccountResponse> response = restTemplate.getForEntity(url, AccountResponse.class);
                AccountResponse acc = response.getBody();

                String fullName = c.getLastName() + " " + c.getFirstName();

                result.add(new UserFullInfoResponse(
                        acc.getId(),
                        acc.getUsername(),
                        acc.getRole(),
                        fullName,
                        c.getEmail(),
                        c.getPhone(),
                        c.getAddress()
                ));
            } catch (Exception e) {
                // Bỏ qua nếu không gọi được auth-service
            }
        }

        return result;
    }

    @Override
    public List<UserFullInfoResponse> searchByRoleAndKeyword(String roleGroup, String keyword) {
        String url = roleGroup.equalsIgnoreCase("USER")
                ? authServiceUrl + "/auth/accounts/users"
                : authServiceUrl + "/auth/accounts/staffs";

        ResponseEntity<AccountResponse[]> response = restTemplate.getForEntity(url, AccountResponse[].class);
        AccountResponse[] accounts = response.getBody();
        if (accounts == null) return Collections.emptyList();

        List<UserFullInfoResponse> result = new ArrayList<>();
        // Lọc theo vai trò
        for (AccountResponse acc : accounts) {
            Optional<CustomerEntity> optional = customerRepository.findByUserId(acc.getId());
            if (optional.isPresent()) {
                CustomerEntity c = optional.get();
                String fullName = c.getLastName() + " " + c.getFirstName();

                // Check if keyword is null or empty → luôn khớp
                boolean match = (keyword == null || keyword.isBlank())
                        || fullName.toLowerCase().contains(keyword.toLowerCase())
                        || c.getPhone().contains(keyword);

                if (match) {
                    result.add(new UserFullInfoResponse(
                            acc.getId(),
                            acc.getUsername(),
                            acc.getRole(),
                            fullName,
                            c.getEmail(),
                            c.getPhone(),
                            c.getAddress()
                    ));
                }
            }
        }

        return result;
    }

    @Override
    public List<UserFullInfoResponse> searchStaffByKeywordAndRole(String keyword, String roleFilter) {
        ResponseEntity<AccountResponse[]> response = restTemplate.getForEntity(
                authServiceUrl + "/auth/accounts/staffs", // luôn lấy STAFF + ADMIN
                AccountResponse[].class
        );

        AccountResponse[] accounts = response.getBody();
        if (accounts == null) return Collections.emptyList();

        List<UserFullInfoResponse> result = new ArrayList<>();

        for (AccountResponse acc : accounts) {
            // Nếu roleFilter không phải ALL và không rỗng thì lọc theo vai trò
            if (roleFilter != null && !roleFilter.equalsIgnoreCase("ALL")
                    && !acc.getRole().equalsIgnoreCase(roleFilter)) {
                continue;
            }

            Optional<CustomerEntity> optional = customerRepository.findByUserId(acc.getId());
            if (optional.isPresent()) {
                CustomerEntity c = optional.get();
                String fullName = c.getLastName() + " " + c.getFirstName();

                // Nếu không có keyword → luôn khớp
                boolean match = (keyword == null || keyword.isBlank())
                        || fullName.toLowerCase().contains(keyword.toLowerCase())
                        || c.getPhone().contains(keyword);

                if (match) {
                    result.add(new UserFullInfoResponse(
                            acc.getId(),
                            acc.getUsername(),
                            acc.getRole(),
                            fullName,
                            c.getEmail(),
                            c.getPhone(),
                            c.getAddress()
                    ));
                }
            }
        }

        return result;
    }

    @Override
    public Optional<CustomerEntity> findByEmailIgnoreCase(String email) {
        return customerRepository.findByEmailIgnoreCase(email);
    }

    private boolean checkUsernameExists(String username) {
        try {
            ResponseEntity<Boolean> response = restTemplate.getForEntity(
                    authServiceUrl + "/auth/accounts/check-username?username=" + username,
                    Boolean.class
            );
            return response.getBody() != null && response.getBody();
        } catch (Exception e) {
            return false;
        }
    }


}


