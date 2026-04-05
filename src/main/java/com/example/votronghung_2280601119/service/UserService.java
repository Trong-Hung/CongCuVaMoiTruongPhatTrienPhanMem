package com.example.votronghung_2280601119.service;

import com.example.votronghung_2280601119.model.User;
import com.example.votronghung_2280601119.model.Company;
import com.example.votronghung_2280601119.repository.UserRepository;
import com.example.votronghung_2280601119.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepo;

    @Autowired
    private CompanyRepository companyRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<User> getUsersByCompany(Long companyId) {
        return userRepo.findByCompanyId(companyId);
    }

    public User getUserById(Long id) {
        return userRepo.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên"));
    }

    public void updateUser(Long id, User updatedDetails) {
        User existingUser = getUserById(id);
        existingUser.setFullName(updatedDetails.getFullName());
        existingUser.setPosition(updatedDetails.getPosition());
        existingUser.setRole(updatedDetails.getRole());
        userRepo.save(existingUser);
    }

    public void deleteUser(Long id) {
        userRepo.deleteById(id);
    }

    public void inviteEmployee(String email, String role, Company company) {
        if (userRepo.findByUsername(email).isPresent()) {
            throw new RuntimeException("Email (Username) này đã tồn tại trong hệ thống!");
        }

        User newUser = new User();
        newUser.setUsername(email);
        newUser.setRole(role);
        newUser.setCompany(company);
        newUser.setFullName("Nhân viên mới");
        newUser.setPosition("Chưa cập nhật");
        userRepo.save(newUser);
    }

    // HÀM ĐĂNG KÝ DOANH NGHIỆP ĐÃ FIX THỨ TỰ VÀ MÃ HÓA
    public void registerCompany(String companyName, String username, String password, String fullName, String position) {
        // 1. Kiểm tra trùng lặp
        if (userRepo.findByUsername(username).isPresent()) {
            throw new RuntimeException("Tên đăng nhập (Email) này đã tồn tại!");
        }

        // 2. Tạo và lưu Công ty
        Company company = new Company();
        company.setName(companyName);
        company = companyRepo.save(company);

        // 3. Tạo tài khoản Admin (Phải gán đúng cột)
        User adminUser = new User();
        adminUser.setUsername(username); // Email vào cột username

        // CỰC KỲ QUAN TRỌNG: Mã hóa mật khẩu BCrypt ở đây
        adminUser.setPassword(passwordEncoder.encode(password));

        adminUser.setFullName(fullName); // Họ tên vào full_name
        adminUser.setPosition(position); // Địa chỉ vào position
        adminUser.setRole("ADMIN");
        adminUser.setCompany(company);

        // 4. Lưu User
        userRepo.save(adminUser);
    }
}