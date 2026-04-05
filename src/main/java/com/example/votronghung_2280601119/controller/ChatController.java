package com.example.votronghung_2280601119.controller;

import com.example.votronghung_2280601119.model.ChatMessage;
import com.example.votronghung_2280601119.model.ChatRoom;
import com.example.votronghung_2280601119.model.User;
import com.example.votronghung_2280601119.repository.ChatMessageRepository;
import com.example.votronghung_2280601119.repository.ChatRoomRepository;
import com.example.votronghung_2280601119.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
// ĐÃ SỬA IMPORT CHUẨN Ở DÒNG DƯỚI NÀY
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.Map;

@Controller
public class ChatController {

    @Autowired private ChatMessageRepository messageRepo;
    @Autowired private UserRepository userRepo;
    @Autowired private ChatRoomRepository roomRepo;

    // ĐÃ ĐỔI KIỂU DỮ LIỆU ĐỂ INTELLIJ KHÔNG BÁO LỖI NỮA
    @Autowired private SimpMessagingTemplate messagingTemplate;

    @GetMapping("/chat")
    public String chatPage(Model model, Authentication authentication) {
        String email = "";
        if (authentication.getPrincipal() instanceof OAuth2User) {
            email = ((OAuth2User) authentication.getPrincipal()).getAttribute("email");
        } else {
            email = authentication.getName();
        }
        User currentUser = userRepo.findByUsername(email).orElseThrow();

        model.addAttribute("user", currentUser);

        if (currentUser.getCompany() != null) {
            model.addAttribute("colleagues", userRepo.findAll());
        }

        return "task/chat";
    }

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessage incomingMessage) {
        User sender = userRepo.findById(incomingMessage.getSender().getId()).orElseThrow();
        ChatRoom room = roomRepo.findById(incomingMessage.getRoom().getId()).orElseThrow();

        incomingMessage.setSender(sender);
        incomingMessage.setRoom(room);
        incomingMessage.setDeleted(false); // Mặc định tin nhắn mới là chưa xóa
        ChatMessage savedMessage = messageRepo.save(incomingMessage);

        Map<String, Object> response = new HashMap<>();
        response.put("id", savedMessage.getId()); // Cần ID để làm nút Xóa
        response.put("content", savedMessage.getContent());
        response.put("isDeleted", savedMessage.isDeleted());

        Map<String, Object> senderInfo = new HashMap<>();
        senderInfo.put("id", sender.getId());
        senderInfo.put("fullName", sender.getFullName());
        response.put("sender", senderInfo);

        messagingTemplate.convertAndSend("/topic/room/" + room.getId(), (Object) response);    }
}