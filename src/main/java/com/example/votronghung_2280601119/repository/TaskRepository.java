package com.example.votronghung_2280601119.repository;

import com.example.votronghung_2280601119.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    // 1. Dùng cho ADMIN: Lấy tất cả task trong công ty theo trạng thái
    List<Task> findByCompanyIdAndStatus(Long companyId, String status);

    // 2. Dùng cho USER: Lấy task mình được giao HOẶC đang theo dõi
    @Query("SELECT DISTINCT t FROM Task t LEFT JOIN t.followers f WHERE t.company.id = :companyId AND t.status = :status AND (t.assignee.id = :userId OR f.id = :userId)")
    List<Task> findTasksForUserBoard(@Param("companyId") Long companyId, @Param("status") String status, @Param("userId") Long userId);
}