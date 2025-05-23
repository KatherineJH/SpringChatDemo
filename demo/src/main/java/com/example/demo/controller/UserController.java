package com.example.demo.controller;

import com.example.demo.dto.UserInfoDTO;
import com.example.demo.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

  private final AuthService userService;

  @GetMapping("/me")
  public ResponseEntity<UserInfoDTO> getCurrentUser(Authentication authentication) {
    return ResponseEntity.ok(userService.getCurrentUserInfo(authentication));
  }
}
