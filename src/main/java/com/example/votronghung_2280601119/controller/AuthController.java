package com.example.votronghung_2280601119.controller;

import com.example.votronghung_2280601119.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {
    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegister() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String email,
                           @RequestParam String password,
                           @RequestParam String fullName,
                           @RequestParam String companyName,
                           @RequestParam String address) {
        // QUAN TRỌNG: Thứ tự truyền vào phải khớp với định nghĩa ở Service bên dưới
        // Thứ tự: Tên công ty, Email, Mật khẩu, Họ tên, Địa chỉ
        userService.registerCompany(companyName, email, password, fullName, address);
        return "redirect:/login?success";
    }
}