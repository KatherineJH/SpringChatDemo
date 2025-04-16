package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "chat_messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "chat_room_id", nullable = false)
  private ChatRoom chatRoom;

  @ManyToOne
  @JoinColumn(name = "sender_id", nullable = false)
  private User sender;

  @Column(nullable = false)
  private String content;

  @Enumerated(EnumType.STRING)
  private MessageType messageType;

  @Column(name = "sent_at", updatable = false)
  private LocalDateTime sentAt;

  @PrePersist
  protected void onCreate() {
    sentAt = LocalDateTime.now();
  }
}
