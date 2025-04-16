package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "chatters")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Chatters {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "user1_id", nullable = false)
  private User user1;

  @ManyToOne
  @JoinColumn(name = "user2_id", nullable = false)
  private User user2;

  @ManyToOne
  @JoinColumn(name = "chat_room_id", nullable = false)
  private ChatRoom chatRoom;
}
