package com.example.votronghung_2280601119.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
// ĐÃ THÊM 2 DÒNG NÀY ĐỂ FIX LỖI
import java.util.List;
import java.util.ArrayList;

@Entity
@Data
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String status; // TODO, IN_PROGRESS, PENDING_REVIEW, SUCCESS, FAILED

    private LocalDateTime deadline;

    // Nhân viên được giao việc
    @ManyToOne
    @JoinColumn(name = "assignee_id")
    private User assignee;

    // Danh sách những người theo dõi (Nhiều - Nhiều)
    @ManyToMany
    @JoinTable(
            name = "task_followers",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> followers = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    // Lưu nội dung kết quả công việc
    @Column(columnDefinition = "TEXT")
    private String result;

    // Danh sách file đính kèm
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TaskAttachment> attachments = new ArrayList<>();
}