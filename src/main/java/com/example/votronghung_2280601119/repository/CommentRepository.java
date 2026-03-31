package com.example.votronghung_2280601119.repository;

import com.example.votronghung_2280601119.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    // Sửa Asc thành Desc để khớp với Controller và đưa bình luận mới nhất lên đầu
    List<Comment> findByTaskIdOrderByCreatedAtDesc(Long taskId);
}