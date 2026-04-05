package com.example.votronghung_2280601119.repository;

import com.example.votronghung_2280601119.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    // Hàm cũ (Lấy lịch sử)
    List<ChatMessage> findByRoomIdOrderByTimestampAsc(Long roomId);

    // THÊM HÀM MỚI: Lấy 1 tin nhắn mới nhất của phòng để sắp xếp
    ChatMessage findFirstByRoomIdOrderByTimestampDesc(Long roomId);
}
