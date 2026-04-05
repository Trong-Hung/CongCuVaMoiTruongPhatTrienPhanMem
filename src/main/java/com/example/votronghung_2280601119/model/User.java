package com.example.votronghung_2280601119.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username; // Email đăng nhập

    private String password;
    private String fullName;
    private String role; // ADMIN (Quản lý), USER (Nhân viên)
    private String position; // Chức vụ (Trưởng phòng, Intern, Dev...)
    private String avatar;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    @ManyToOne
    @JoinColumn(name = "dept_id")
    private Department department;

    @lombok.EqualsAndHashCode.Exclude
    @lombok.ToString.Exclude
    @ManyToMany(mappedBy = "members", fetch = FetchType.LAZY)
    private java.util.Set<ChatRoom> chatRooms = new java.util.HashSet<>();


}