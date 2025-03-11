package com.user.userservice.config;

import com.user.userservice.security.CustomSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Câu Hình Test API với Postman
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/users/list").permitAll()
                        .requestMatchers("/users/register").permitAll()  // Cho phép đăng ký mà không cần đăng nhập
                        .requestMatchers("/users/change-password/**").permitAll()
                        .requestMatchers("/api/private/**").authenticated()
                        .requestMatchers("/admin/**").hasRole("MANAGER")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")  // Định nghĩa trang login vẫn chu sai
                        .permitAll()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                );
                http.httpBasic(httpBasic -> httpBasic.disable());
        return http.build();
    }

    // Câu hình xác thực
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(csrf -> csrf.disable())  // Cách mới để vô hiệu hóa CSRF
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/login", "/resource/**", "/trang-chu", "/api/**").permitAll()
//                )
//                .formLogin(form -> form
//                        .loginPage("/login")
//                        .usernameParameter("j_username")
//                        .passwordParameter("j_password")
//                        .loginProcessingUrl("/j_spring_security_check")
//                        .successHandler(myAuthenticationSuccessHandler())
//                        .failureUrl("/login?incorrectAccount")
//                )
//                .logout(logout -> logout
//                        .logoutUrl("/logout")
//                        .deleteCookies("JSESSIONID")
//                )
//                .exceptionHandling(ex -> ex
//                        .accessDeniedPage("/access-denied")
//                )
//                .sessionManagement(session -> session
//                        .maximumSessions(1)
//                        .expiredUrl("/login?sessionTimeout")
//                );
//
//        return http.build();
//    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public AuthenticationSuccessHandler myAuthenticationSuccessHandler(){
        return new CustomSuccessHandler();
    }
}
