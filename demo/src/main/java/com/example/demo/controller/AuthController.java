package com.example.demo.controller;

import com.example.demo.dto.AuthRequest;
import com.example.demo.dto.AuthResponse;
import com.example.demo.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  /**
   * XSS로부터 안전(자바스크립트 접근 불가)한 HTTP Cookie(주로 HttpOnly, Secure 옵션)을 사용. 방식: 서버가 JWT를 Set-Cookie로 내려줌
   * -> 이후 요청에서 브라우저가 자동으로 쿠키를 붙여서 전송. 1. HttpOnly, Secure 옵션을 잘 쓰면 XSS/CSRF에 강해짐->악성 스크립트가 토큰 훔쳐가기
   * 어려움. 2. 브라우저가 자동으로 쿠키 붙이니까 프론트 코드도 깔끔해짐. 3. SameSite 옵션을 Lax 또는 Strict로 하면 CSRF도 방어 가능.
   */
  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(
      @RequestBody AuthRequest request, HttpServletResponse response) {
    AuthResponse auth = authService.login(request);

    Cookie jwtCookie = new Cookie("Authorization", auth.getToken());
    jwtCookie.setHttpOnly(true);
    jwtCookie.setSecure(false); // 개발 중에는 false로 설정(http://에서는 안됨)
    jwtCookie.setPath("/");
    jwtCookie.setMaxAge(60 * 60);
    jwtCookie.setDomain("localhost");
    response.setHeader(
        "Set-Cookie",
        "Authorization=" + auth.getToken() + "; Path=/; Max-Age=3600; HttpOnly; SameSite=Lax");
    response.addCookie(jwtCookie);

    // ✅ 응답 본문에 토큰 등을 포함
    return ResponseEntity.ok(auth);
  }

  @PostMapping("/register")
  public ResponseEntity<Void> register(@RequestBody AuthRequest request) {
    authService.register(request);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/oauth2/success")
  public ResponseEntity<AuthResponse> oauth2Success(@RequestParam String token) {
    return ResponseEntity.ok(AuthResponse.builder().token(token).build());
  }

  /**
   * HttpOnly 쿠키에 저장된 JWT 토큰을 클라이언트가 직접 꺼내올 수 없기 때문에, 서버에게 토큰을 요청해서 JS 코드에서 사용 웹소켓 연결, 로그인 후 /chat
   * 페이지로 넘어갔을 때 토큰을 꺼내 STOMP 연결 등에서 사용.
   */
  @GetMapping("/token")
  public ResponseEntity<Map<String, String>> getTokenFromCookie(HttpServletRequest request) {
    // ✅ 요청에 포함된 쿠키 배열 가져오기
    Cookie[] cookies = request.getCookies();
    if (cookies == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(Map.of("error", "No cookies found"));
    }
    // ✅ 쿠키 중에서 이름이 "Authorization"인 값을 찾아서 JWT 토큰 추출
    String token =
        Arrays.stream(cookies)
            .filter(cookie -> "Authorization".equals(cookie.getName()))
            .map(Cookie::getValue)
            .findFirst()
            .orElse(null);
    if (token == null || token.isBlank()) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(Map.of("error", "Authorization token not found"));
    }
    return ResponseEntity.ok(Map.of("token", token));
  }
}
