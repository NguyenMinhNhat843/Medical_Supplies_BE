package com.user.userservice.feign;

import com.user.userservice.model.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface AuthServiceClient {
    @GetMapping("/users/{id}")
    UserDTO getAccountById(@PathVariable Long userId);
}

