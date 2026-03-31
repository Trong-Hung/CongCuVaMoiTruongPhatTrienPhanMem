package com.example.votronghung_2280601119.repository;

import com.example.votronghung_2280601119.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    // Lấy tất cả phòng ban thuộc về 1 công ty cụ thể
    List<Department> findByCompanyId(Long companyId);
}