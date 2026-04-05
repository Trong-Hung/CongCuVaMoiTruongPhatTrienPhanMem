package com.example.votronghung_2280601119.controller;

import com.example.votronghung_2280601119.model.ChatMessage;
import com.example.votronghung_2280601119.model.ChatRoom;
import com.example.votronghung_2280601119.model.User;
import com.example.votronghung_2280601119.repository.ChatMessageRepository;
import com.example.votronghung_2280601119.repository.ChatRoomRepository;
import com.example.votronghung_2280601119.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chat")
public class ChatRestController {

    @Autowired private ChatMessageRepository messageRepo;
    @Autowired private ChatRoomRepository roomRepo;
    @Autowired private UserRepository userRepo;

    // 1. API kéo lịch sử tin nhắn (Đã che tin nhắn bị thu hồi)
    @GetMapping("/history/{roomId}")
    public List<Map<String, Object>> getChatHistory(@PathVariable Long roomId) {
        List<ChatMessage> messages = messageRepo.findByRoomIdOrderByTimestampAsc(roomId);
        return messages.stream().map(msg -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", msg.getId());

            // Nếu bị xóa rồi thì che đi
            if (msg.isDeleted()) {
                map.put("content", "🚫 Tin nhắn đã bị thu hồi");
                map.put("isDeleted", true);
            } else {
                map.put("content", msg.getContent());
                map.put("isDeleted", false);
            }

            Map<String, Object> sender = new HashMap<>();
            sender.put("id", msg.getSender().getId());
            sender.put("fullName", msg.getSender().getFullName());
            map.put("sender", sender);
            return map;
        }).collect(Collectors.toList());
    }

    // 2. API THU HỒI TIN NHẮN (MỚI)
    @DeleteMapping("/recall/{msgId}")
    @Transactional
    public String recallMessage(@PathVariable Long msgId) {
        ChatMessage msg = messageRepo.findById(msgId).orElseThrow();
        msg.setDeleted(true);
        messageRepo.save(msg);
        return "OK";
    }

    @Transactional
    @GetMapping("/get-or-create-room")
    public Long getOrCreateRoom(@RequestParam Long user1Id, @RequestParam Long user2Id) {
        User u1 = userRepo.findById(user1Id).orElseThrow();
        User u2 = userRepo.findById(user2Id).orElseThrow();

        for (ChatRoom room : u1.getChatRooms()) {
            if (!room.isGroupChat() && room.getMembers().contains(u2)) {
                return room.getId();
            }
        }

        ChatRoom newRoom = new ChatRoom();
        newRoom.setGroupChat(false);
        newRoom.setName(u1.getFullName() + " & " + u2.getFullName());
        newRoom.getMembers().add(u1);
        newRoom.getMembers().add(u2);
        roomRepo.save(newRoom);
        return newRoom.getId();
    }

    @PostMapping("/create-group")
    @Transactional
    public Long createGroupChat(@RequestBody Map<String, Object> payload) {
        String groupName = (String) payload.get("name");
        List<Integer> memberIdsInt = (List<Integer>) payload.get("memberIds");

        ChatRoom newGroup = new ChatRoom();
        newGroup.setGroupChat(true);
        newGroup.setName(groupName);

        for (Integer id : memberIdsInt) {
            User user = userRepo.findById(id.longValue()).orElseThrow();
            newGroup.getMembers().add(user);
        }

        roomRepo.save(newGroup);
        return newGroup.getId();
    }

    // 4. API LẤY PHÒNG CHAT (ĐÃ SẮP XẾP PHÒNG CÓ TIN NHẮN MỚI LÊN ĐẦU)
    @GetMapping("/my-rooms")
    @Transactional
    public List<Map<String, Object>> getMyRooms(@RequestParam Long userId) {
        User currentUser = userRepo.findById(userId).orElseThrow();
        List<Map<String, Object>> result = new ArrayList<>();

        for (ChatRoom room : currentUser.getChatRooms()) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", room.getId());
            map.put("isGroup", room.isGroupChat());

            if (room.isGroupChat()) {
                map.put("name", room.getName());
            } else {
                String otherName = "Trò chuyện ẩn";
                for (User member : room.getMembers()) {
                    if (!member.getId().equals(userId)) {
                        otherName = member.getFullName(); break;
                    }
                }
                map.put("name", otherName);
            }

            // TÌM TIN NHẮN CUỐI CÙNG ĐỂ SẮP XẾP
            List<ChatMessage> msgs = messageRepo.findByRoomIdOrderByTimestampAsc(room.getId());
            java.time.LocalDateTime lastActive = msgs.isEmpty() ? room.getCreatedAt() : msgs.get(msgs.size()-1).getTimestamp();

            if(room.getId() == 1L) lastActive = java.time.LocalDateTime.now().plusYears(100);

            map.put("lastActive", lastActive);
            result.add(map);
        }

        result.sort((r1, r2) -> {
            java.time.LocalDateTime t1 = (java.time.LocalDateTime) r1.get("lastActive");
            java.time.LocalDateTime t2 = (java.time.LocalDateTime) r2.get("lastActive");
            return t2.compareTo(t1);
        });

        return result;
    }
}