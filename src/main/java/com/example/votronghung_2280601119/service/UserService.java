package com.example.votronghung_2280601119.service;

import com.example.votronghung_2280601119.model.User;
import com.example.votronghung_2280601119.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepo;

    public List<User> getUsersByCompany(Long companyId) {
        return userRepo.findByCompanyId(companyId);
    }

    public User getUserById(Long id) {
        return userRepo.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên"));
    }

    // Lưu thông tin nhân viên sau khi sửa
    public void updateUser(Long id, User updatedDetails) {
        User existingUser = getUserById(id);
        existingUser.setFullName(updatedDetails.getFullName());
        existingUser.setPosition(updatedDetails.getPosition());
        existingUser.setRole(updatedDetails.getRole());
        // Thêm các trường khác nếu cần
        userRepo.save(existingUser);
    }
    public void deleteUser(Long id) {
        userRepo.deleteById(id);
    }
    // Bổ sung hàm này vào UserService
    public void inviteEmployee(String email, String role, com.example.votronghung_2280601119.model.Company company) {
        // Kiểm tra xem user (email) đã tồn tại trong database chưa
        if (userRepo.findByUsername(email).isPresent()) {
            throw new RuntimeException("Email (Username) này đã tồn tại trong hệ thống!");
        }

        // Tạo mới một nhân viên
        User newUser = new User();
        newUser.setUsername(email); // Dùng email làm username
        newUser.setRole(role);
        newUser.setCompany(company);

        // Gán các giá trị mặc định cho nhân viên mới
        newUser.setFullName("Nhân viên mới");
        newUser.setPosition("Chưa cập nhật");

        // Lưu ý: Nếu hệ thống của bạn yêu cầu mật khẩu để login (không dùng Google),
        // bạn có thể set một mật khẩu mặc định tại đây (nhớ dùng PasswordEncoder nếu có).
        // newUser.setPassword("123456");

        userRepo.save(newUser);
    }
    // Nếu trong UserService chưa có companyRepo thì bạn nhớ thêm dòng này ở trên cùng nhé:
    @Autowired
    private com.example.votronghung_2280601119.repository.CompanyRepository companyRepo;

    // Bổ sung hàm registerCompany
    public void registerCompany(String companyName, String username, String password, String fullName, String position) {
        // 1. Kiểm tra xem username (email) đã được sử dụng chưa
        if (userRepo.findByUsername(username).isPresent()) {
            throw new RuntimeException("Tên đăng nhập (Email) này đã tồn tại!");
        }

        // 2. Tạo mới Công ty
        com.example.votronghung_2280601119.model.Company company = new com.example.votronghung_2280601119.model.Company();
        company.setName(companyName);
        // Lưu công ty vào DB trước để lấy ID
        company = companyRepo.save(company);

        // 3. Tạo tài khoản Chủ quản lý (ADMIN) cho công ty vừa tạo
        User adminUser = new User();
        adminUser.setUsername(username);
        adminUser.setPassword(password); // Mẹo: Nếu project yêu cầu bảo mật, chỗ này nên dùng PasswordEncoder để mã hóa
        adminUser.setFullName(fullName);
        adminUser.setPosition(position);
        adminUser.setRole("ADMIN"); // Người tạo công ty chắc chắn là Quản lý
        adminUser.setCompany(company);

        // Lưu tài khoản Admin vào DB
        userRepo.save(adminUser);
    }
}