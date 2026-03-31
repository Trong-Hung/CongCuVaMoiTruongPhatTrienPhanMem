package com.example.votronghung_2280601119.controller;

import com.example.votronghung_2280601119.model.User;
import com.example.votronghung_2280601119.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
// @RequestMapping("/profile") // Không cần RequestMapping ở mức class vì người dùng không có
public class UserController {

    @Autowired
    private UserService userService;

    // Trang Profile cá nhân (Giữ nguyên của người dùng)
    @GetMapping("/profile")
    public String profile(Model model, @AuthenticationPrincipal Object principal) {
        if (principal instanceof OAuth2User) {
            OAuth2User oauth2User = (OAuth2User) principal;
            // Lấy thông tin từ Google
            model.addAttribute("name", oauth2User.getAttribute("name"));
            model.addAttribute("email", oauth2User.getAttribute("email"));
            model.addAttribute("picture", oauth2User.getAttribute("picture"));
        } else {
            // Trường hợp đăng nhập thường (Form Login)
            model.addAttribute("name", "Người dùng hệ thống");
            model.addAttribute("email", "N/A");
            model.addAttribute("picture", "https://cdn-icons-png.flaticon.com/512/3135/3135715.png");
        }
        return "task/profile";
    }

    // PHẦN MỚI THÊM: XEM CHI TIẾT NHÂN VIÊN
    @GetMapping("/users/detail/{id}")
    public String viewEmployeeDetail(@PathVariable Long id, Model model) {
        User employee = userService.getUserById(id);
        model.addAttribute("employee", employee);
        return "task/user-detail"; // Tạo một view mới user-detail.html
    }
}