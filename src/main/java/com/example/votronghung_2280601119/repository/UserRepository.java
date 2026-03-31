package com.example.votronghung_2280601119.repository;

import com.example.votronghung_2280601119.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    List<User> findByCompanyId(Long companyId); // Lấy user theo công ty
}