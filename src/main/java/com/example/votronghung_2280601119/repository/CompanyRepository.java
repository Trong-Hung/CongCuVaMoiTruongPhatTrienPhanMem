package com.example.votronghung_2280601119.repository;

import com.example.votronghung_2280601119.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    // Tìm công ty theo tên nếu cần kiểm tra trùng lặp
    boolean existsByName(String name);
}