package com.example.demo.service;

import com.example.demo.dto.AuthRequest;
import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.UserInfoDTO;
import com.example.demo.entity.User;
import com.example.demo.jwt.JwtUtil;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final AuthenticationManager authenticationManager;
  private final JwtUtil jwtUtil;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public AuthResponse login(AuthRequest request) {
    Authentication authentication =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
    String token = jwtUtil.generateToken(authentication.getName());
    return AuthResponse.builder().token(token).build();
  }

  public void register(AuthRequest request) {
    User user =
        User.builder()
            .username(request.getUsername())
            .password(passwordEncoder.encode(request.getPassword()))
            .email(request.getUsername()) // 이메일로 사용
            .provider("email")
            .build();
    userRepository.save(user);
  }

  public UserInfoDTO getCurrentUserInfo(Authentication authentication) {
    String username = authentication.getName();
    User user =
        userRepository
            .findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found: " + username));

    return UserInfoDTO.builder()
        .id(user.getId())
        .username(user.getUsername())
        .email(user.getEmail())
        .createdAt(user.getCreatedAt())
        .build();
  }
}
