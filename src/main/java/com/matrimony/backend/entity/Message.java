package com.matrimony.backend.entity;

import com.matrimony.backend.enums.MessageType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "messages", indexes = @Index(name = "idx_message_conversation", columnList = "conversation_id"))
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sender_profile_id", nullable = false)
    private MatrimonyProfile senderProfile;
    @Column(name = "message_text", length = 2000)
    private String messageText;
    @Enumerated(EnumType.STRING)
    @Column(name = "message_type", nullable = false, length = 30)
    private MessageType messageType = MessageType.TEXT;
    private String attachmentUrl;
    @Column(nullable = false)
    private LocalDateTime sentAt;
    private LocalDateTime readAt;
    @Column(nullable = false)
    private boolean deletedBySender;
    @Column(nullable = false)
    private boolean deletedByReceiver;
}
