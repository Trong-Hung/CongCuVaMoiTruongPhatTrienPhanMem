package com.example.votronghung_2280601119.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        // Tự động chuyển hướng đường dẫn gốc (localhost:8080) vào hệ thống
        return "redirect:/tasks/board";
    }
}