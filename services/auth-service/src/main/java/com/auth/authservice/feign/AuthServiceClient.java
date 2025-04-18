package com.auth.authservice.feign;

import com.auth.authservice.model.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "auth-service")
public interface AuthServiceClient {
    @GetMapping("/users/{id}")
    UserDTO getAccountById(@PathVariable Long userId);
}

