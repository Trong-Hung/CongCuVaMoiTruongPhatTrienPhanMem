package com.example.votronghung_2280601119.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TaskRequest {
    private String title;
    private String description;
    private LocalDateTime deadline;
    private Long assigneeId;
    private Long followerId;
}