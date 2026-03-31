package com.example.votronghung_2280601119.service;

import com.example.votronghung_2280601119.model.Task;
import com.example.votronghung_2280601119.model.User;
import com.example.votronghung_2280601119.repository.TaskRepository;
import com.example.votronghung_2280601119.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class TaskService {
    @Autowired private TaskRepository taskRepo;
    @Autowired private UserRepository userRepo;

    // Lấy danh sách Task ĐÃ ĐƯỢC LỌC CHỈ CHO USER ĐÓ THẤY
    // Thay thế hàm lấy danh sách Task trên Board bằng hàm này:
    public List<Task> getTasksForBoard(User user, String status) {
        Long companyId = user.getCompany().getId();

        // Kiểm tra quyền: Nếu là ADMIN thì gọi hàm lấy tất cả
        if ("ADMIN".equals(user.getRole())) {
            return taskRepo.findByCompanyIdAndStatus(companyId, status);
        }
        // Nếu là USER bình thường thì gọi hàm lọc
        else {
            return taskRepo.findTasksForUserBoard(companyId, status, user.getId());
        }
    }

    public Task getTaskById(Long id) {
        return taskRepo.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy công việc"));
    }

    // Cập nhật để nhận 1 List ID những người theo dõi
    public void saveTask(Task task, Long assigneeId, List<Long> followerIds) {
        if (assigneeId != null) {
            task.setAssignee(userRepo.findById(assigneeId).orElse(null));
        }

        // Nếu có chọn người theo dõi thì tìm và gán vào Set
        if (followerIds != null && !followerIds.isEmpty()) {
            Set<User> followers = new HashSet<>(userRepo.findAllById(followerIds));
            task.setFollowers(followers);
        } else {
            task.setFollowers(new HashSet<>()); // Xóa sạch nếu không chọn ai
        }

        if (task.getId() == null) {
            task.setStatus("TODO");
        }

        taskRepo.save(task);
    }

    public void submitTask(Long taskId) {
        Task task = getTaskById(taskId);
        task.setStatus("PENDING_REVIEW");
        taskRepo.save(task);
    }

    public void reviewTask(Long taskId, boolean isPassed) {
        Task task = getTaskById(taskId);
        if (isPassed) {
            task.setStatus("SUCCESS");
        } else {
            task.setStatus("FAILED");
        }
        taskRepo.save(task);
    }
}