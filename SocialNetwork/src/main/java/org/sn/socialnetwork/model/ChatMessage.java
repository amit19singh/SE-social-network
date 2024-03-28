package org.sn.socialnetwork.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "chatMessage")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String senderUsername;
    private String receiverUsername;
    private String message;
    private LocalDateTime timestamp;
    private MessageStatus status;

    enum MessageStatus {
        SENT,
        DELIVERED,
        READ,
        JOIN
    }
}

