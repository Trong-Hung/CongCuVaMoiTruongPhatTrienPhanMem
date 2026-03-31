package com.example.votronghung_2280601119.controller;

import com.example.votronghung_2280601119.model.User;
import com.example.votronghung_2280601119.repository.UserRepository;
import com.example.votronghung_2280601119.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired private UserService userService;
    @Autowired private UserRepository userRepo;

    // Xem danh sách nhân viên của công ty mình
    @GetMapping("/users")
    public String listUsers(Model model, Principal principal) {
        User admin = userRepo.findByUsername(principal.getName()).orElseThrow();
        model.addAttribute("employees", userRepo.findByCompanyId(admin.getCompany().getId()));
        model.addAttribute("companyName", admin.getCompany().getName());
        return "admin/user-list";
    }

    // Mời nhân viên mới
    @PostMapping("/add-employee")
    public String addEmployee(@RequestParam String email, @RequestParam String fullName, Principal principal) {
        User admin = userRepo.findByUsername(principal.getName()).orElseThrow();
        userService.inviteEmployee(email, fullName, admin.getCompany());
        return "redirect:/admin/users?invited";
    }

    // Xóa nhân viên
    @GetMapping("/users/delete/{id}")
    public String deleteEmployee(@PathVariable Long id) {
        userRepo.deleteById(id);
        return "redirect:/admin/users";
    }
}