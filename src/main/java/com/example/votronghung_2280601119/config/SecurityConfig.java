package com.example.votronghung_2280601119.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // THÊM IMPORT NÀY
import org.springframework.security.crypto.password.PasswordEncoder;   // THÊM IMPORT NÀY
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    // --- ĐÂY LÀ ĐOẠN HÙNG ĐANG THIẾU ---
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    // ----------------------------------

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                // Trong file SecurityConfig.java, phần authorizeHttpRequests:
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/register", "/css/**", "/js/**", "/uploads/**", "/ws/**").permitAll() // Đảm bảo có /ws/**
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/tasks/board", true)
                        .permitAll()
                )
                .oauth2Login(oauth -> oauth
                        .loginPage("/login")
                        .defaultSuccessUrl("/tasks/board", true)
                )
                .logout(logout -> logout.logoutSuccessUrl("/login").permitAll());
        return http.build();
    }
}