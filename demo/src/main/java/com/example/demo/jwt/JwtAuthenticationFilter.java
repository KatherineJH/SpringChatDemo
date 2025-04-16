package com.example.demo.jwt;

import com.example.demo.config.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtUtil jwtUtil;
  private final CustomUserDetailsService userDetailsService;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String token = null;
    String username = null;

    // 1. 헤더에서 꺼내기(한 가지 방식을 채택하는 것이 권장되는 방식이므로 주석처리)
    // 필요한 상황이 생기는 경우에는 주석을 풀고 사용하세요.
    //        String authHeader = request.getHeader("Authorization");
    //        if (authHeader != null && authHeader.startsWith("Bearer ")) {
    //            token = authHeader.substring(7);
    //        }

    // 2. HttpOnly 쿠키에서 JWT 꺼내기(안전한 방식)
    if (request.getCookies() != null) {
      for (Cookie cookie : request.getCookies()) {
        if ("Authorization".equals(cookie.getName())) {
          token = cookie.getValue();
          break;
        }
      }
    }

    // 3. 토큰 유효성 검사 및 인증 처리
    if (token != null) {
      username = jwtUtil.getUsernameFromToken(token);

      if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if (jwtUtil.validateToken(token)) {
          UsernamePasswordAuthenticationToken authToken =
              new UsernamePasswordAuthenticationToken(
                  userDetails, null, userDetails.getAuthorities());
          authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
          // ✅ Spring Security 인증 등록
          SecurityContextHolder.getContext().setAuthentication(authToken);
        }
      }
    }
    // ✅ 다음 필터 체인으로 넘기기
    filterChain.doFilter(request, response);
  }
}
