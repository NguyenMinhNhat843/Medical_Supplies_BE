package com.user.userservice.client;

import com.user.userservice.model.dto.CustomerEmailDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Component
public class CustomerClient {

    @Autowired
    @LoadBalanced
    private RestTemplate restTemplate;

    public Optional<CustomerEmailDTO> getCustomerByEmail(String email) {
        try {
            ResponseEntity<CustomerEmailDTO> response = restTemplate.getForEntity(
                    "http://USER-SERVICE/users/email?value=" + email,
                    CustomerEmailDTO.class
            );
            return Optional.ofNullable(response.getBody());
        } catch (HttpClientErrorException.NotFound e) {
            return Optional.empty();
        }
    }
}