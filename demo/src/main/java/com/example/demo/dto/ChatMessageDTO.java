package com.example.demo.dto;

import com.example.demo.entity.MessageType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageDTO {
  private Long id;
  private Long chatRoomId;
  private String senderUsername;
  private String content;
  private MessageType type;
  private String sentAt;
}
