package com.lms.notificationservice.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "outbox")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Outbox {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageId;

    private Long userId;

    private String channel;

    private String status;

    @Column(columnDefinition = "TEXT")
    private String payloadJson;
} 