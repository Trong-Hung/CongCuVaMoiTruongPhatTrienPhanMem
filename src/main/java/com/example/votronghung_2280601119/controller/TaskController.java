package com.example.votronghung_2280601119.controller;

import com.example.votronghung_2280601119.model.Task;
import com.example.votronghung_2280601119.model.User;
import com.example.votronghung_2280601119.model.Comment;
import com.example.votronghung_2280601119.model.TaskAttachment;
import com.example.votronghung_2280601119.repository.TaskRepository;
import com.example.votronghung_2280601119.repository.UserRepository;
import com.example.votronghung_2280601119.repository.CommentRepository;
import com.example.votronghung_2280601119.repository.TaskAttachmentRepository;
import com.example.votronghung_2280601119.service.TaskService;
import com.example.votronghung_2280601119.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/tasks")
public class TaskController {
    @Autowired private TaskService taskService;
    @Autowired private UserService userService;
    @Autowired private UserRepository userRepo;
    @Autowired private TaskRepository taskRepo;
    @Autowired private CommentRepository commentRepo;

    // Đã thêm cái này để thao tác với File
    @Autowired private TaskAttachmentRepository attachmentRepo;

    @GetMapping("/board")
    public String board(Model model, Principal principal) {
        User user = userRepo.findByUsername(principal.getName()).orElseThrow();
        model.addAttribute("todo", taskService.getTasksForBoard(user, "TODO"));
        model.addAttribute("inProgress", taskService.getTasksForBoard(user, "IN_PROGRESS"));
        model.addAttribute("pendingReview", taskService.getTasksForBoard(user, "PENDING_REVIEW"));
        model.addAttribute("success", taskService.getTasksForBoard(user, "SUCCESS"));
        model.addAttribute("failed", taskService.getTasksForBoard(user, "FAILED"));
        model.addAttribute("user", user);
        return "task/board";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model, Principal principal) {
        User currentUser = userRepo.findByUsername(principal.getName()).orElseThrow();
        model.addAttribute("task", new Task());
        model.addAttribute("employees", userService.getUsersByCompany(currentUser.getCompany().getId()));
        return "task/task-form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, Principal principal) {
        User currentUser = userRepo.findByUsername(principal.getName()).orElseThrow();
        model.addAttribute("task", taskService.getTaskById(id));
        model.addAttribute("employees", userService.getUsersByCompany(currentUser.getCompany().getId()));
        return "task/task-form";
    }

    @PostMapping("/save")
    public String saveTask(@ModelAttribute Task task,
                           @RequestParam(required = false) Long assigneeId,
                           @RequestParam(required = false) List<Long> followerIds,
                           Principal principal) {
        User currentUser = userRepo.findByUsername(principal.getName()).orElseThrow();
        task.setCompany(currentUser.getCompany());
        taskService.saveTask(task, assigneeId, followerIds);
        return "redirect:/tasks/board";
    }

    @GetMapping("/detail/{id}")
    public String viewTaskDetail(@PathVariable Long id, Model model) {
        Task task = taskService.getTaskById(id);
        model.addAttribute("task", task);
        model.addAttribute("comments", commentRepo.findByTaskIdOrderByCreatedAtDesc(id));
        return "task/task-detail";
    }

    @PostMapping("/update-status")
    public String updateStatusFromDetail(@RequestParam Long taskId, @RequestParam String status) {
        Task task = taskService.getTaskById(taskId);
        task.setStatus(status);
        taskRepo.save(task);
        return "redirect:/tasks/detail/" + taskId;
    }

    @PostMapping("/comment")
    public String addComment(@RequestParam Long taskId, @RequestParam String content, Principal principal) {
        User user = userRepo.findByUsername(principal.getName()).orElseThrow();
        Task task = taskService.getTaskById(taskId);
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setTask(task);
        comment.setUser(user);
        comment.setCreatedAt(LocalDateTime.now());
        commentRepo.save(comment);
        return "redirect:/tasks/detail/" + taskId;
    }

    @GetMapping("/submit/{id}")
    public String submit(@PathVariable Long id) {
        taskService.submitTask(id);
        return "redirect:/tasks/board";
    }

    @GetMapping("/review/{id}")
    public String review(@PathVariable Long id, @RequestParam boolean isPassed) {
        taskService.reviewTask(id, isPassed);
        return "redirect:/tasks/board";
    }

    // ================= CÁC TÍNH NĂNG MỚI (LƯU KẾT QUẢ VÀ UPLOAD FILE) =================

    @PostMapping("/update-result")
    @ResponseBody
    public String updateResult(@RequestParam Long taskId, @RequestParam String result) {
        Task task = taskRepo.findById(taskId).orElseThrow();
        task.setResult(result);
        taskRepo.save(task);
        return "OK";
    }

    @PostMapping("/upload-file")
    @ResponseBody
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("taskId") Long taskId) {
        try {
            // 1. Tạo thư mục lưu trữ (Thư mục uploads nằm ngang hàng với src)
            String uploadDir = "uploads/";
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            // 2. Viết file vào ổ cứng máy tính
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path path = Paths.get(uploadDir + fileName);
            Files.write(path, file.getBytes());

            // 3. Lưu thông tin File vào Database
            Task task = taskRepo.findById(taskId).orElseThrow();
            TaskAttachment attachment = new TaskAttachment();
            attachment.setFileName(file.getOriginalFilename());
            attachment.setFileUrl("/" + uploadDir + fileName);
            attachment.setTask(task);
            attachmentRepo.save(attachment);

            return ResponseEntity.ok("Thành công");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi upload: " + e.getMessage());
        }
    }
}