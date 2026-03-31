package com.example.votronghung_2280601119.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Data
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // Tên công ty/doanh nghiệp
    private String address; // Địa chỉ trụ sở
    private String taxCode; // Mã số thuế (nếu cần)

    // Một công ty có nhiều nhân viên
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    private List<User> employees;

    // Một công ty có nhiều phòng ban
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    private List<Department> departments;
}