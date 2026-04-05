package com.example.votronghung_2280601119.controller;

import com.example.votronghung_2280601119.model.User;
import com.example.votronghung_2280601119.repository.UserRepository;
import com.example.votronghung_2280601119.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/users")
public class AdminController {

    @Autowired private UserService userService;
    @Autowired private UserRepository userRepo;
    @Autowired private PasswordEncoder passwordEncoder;

    // 1. Hiển thị danh sách nhân sự của công ty
    @GetMapping
    public String listUsers(Model model, Authentication authentication) {
        String email = "";
        if (authentication.getPrincipal() instanceof OAuth2User) {
            email = ((OAuth2User) authentication.getPrincipal()).getAttribute("email");
        } else {
            email = authentication.getName();
        }

        User admin = userRepo.findByUsername(email).orElseThrow();

        if (admin.getCompany() != null) {
            model.addAttribute("users", userService.getUsersByCompany(admin.getCompany().getId()));
        } else {
            model.addAttribute("users", java.util.List.of());
        }

        model.addAttribute("user", admin);
        return "admin/user-list";
    }

    // 2. Hiển thị Form sửa thông tin nhân viên
    @GetMapping("/edit/{id}")
    public String editUserForm(@PathVariable Long id, Model model) {
        model.addAttribute("employee", userService.getUserById(id));
        return "admin/user-edit";
    }

    // 3. Xử lý lưu thông tin sau khi sửa
    @PostMapping("/update")
    public String updateUser(@ModelAttribute User employee) {
        userService.updateUser(employee.getId(), employee);
        return "redirect:/admin/users";
    }

    // 4. Hiển thị Form Thêm nhân viên mới
    @GetMapping("/create")
    public String showCreateUserForm(Model model) {
        model.addAttribute("employee", new User());
        return "admin/user-add";
    }

    // 5. Xử lý lưu nhân viên mới vào Database
    @PostMapping("/save")
    public String saveNewUser(@ModelAttribute User employee, Authentication authentication) {
        String adminEmail = "";
        if (authentication.getPrincipal() instanceof OAuth2User) {
            adminEmail = ((OAuth2User) authentication.getPrincipal()).getAttribute("email");
        } else {
            adminEmail = authentication.getName();
        }
        User admin = userRepo.findByUsername(adminEmail).orElseThrow();

        employee.setCompany(admin.getCompany());
        employee.setPassword(passwordEncoder.encode("123456")); // Pass mặc định

        if (employee.getAvatar() == null || employee.getAvatar().isEmpty()) {
            employee.setAvatar("https://cdn-icons-png.flaticon.com/512/3135/3135715.png");
        }

        userRepo.save(employee);
        return "redirect:/admin/users";
    }

    // 6. Xóa nhân viên (Đã tích hợp xử lý lỗi khi nhân viên đang dính Task)
    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("successMsg", "Đã xóa nhân viên thành công!");
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("errorMsg", "CẢNH BÁO: Không thể xóa! Nhân viên này đang có công việc. Vui lòng phân công lại công việc của họ trước khi xóa.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Đã xảy ra lỗi hệ thống khi xóa nhân viên.");
        }
        return "redirect:/admin/users";
    }
}