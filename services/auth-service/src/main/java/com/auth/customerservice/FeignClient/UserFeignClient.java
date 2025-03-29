package com.auth.customerservice.FeignClient;

//import com.auth.customerservice.config.FeignClientConfig;
import com.auth.customerservice.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

//@FeignClient(name = "user-service",configuration = FeignClientConfig.class)
public interface UserFeignClient {
    @GetMapping("/users/{id}")
    ResponseEntity<UserDTO> getAccountById(@PathVariable Long userId,@RequestHeader("Authorization") String token);
}