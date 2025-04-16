package com.example.demo.dto;

import java.util.List;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomDTO {
  private Long id;
  private String name;
  private List<String> participants;
}
