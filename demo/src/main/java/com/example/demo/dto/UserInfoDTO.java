package com.example.demo.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserInfoDTO {
  private Long id;
  private String username;
  private String email;
  private LocalDateTime createdAt;
}
