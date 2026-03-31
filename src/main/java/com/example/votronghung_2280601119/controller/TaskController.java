package com.example.votronghung_2280601119.controller;

import com.example.votronghung_2280601119.model.Task;
import com.example.votronghung_2280601119.model.User;
import com.example.votronghung_2280601119.model.Comment;
import com.example.votronghung_2280601119.repository.TaskRepository;
import com.example.votronghung_2280601119.repository.UserRepository;
import com.example.votronghung_2280601119.repository.CommentRepository;
import com.example.votronghung_2280601119.service.TaskService;
import com.example.votronghung_2280601119.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/board")
    public String board(Model model, Principal principal) {
        User user = userRepo.findByUsername(principal.getName()).orElseThrow();

        // Truyền thẳng Object User vào Service để nó tự phân xử quyền
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

    // Đã cập nhật tham số followerIds thành List<Long>
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
}