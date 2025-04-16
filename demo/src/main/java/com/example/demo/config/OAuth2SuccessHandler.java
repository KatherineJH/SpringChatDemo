package com.example.demo.config;

import com.example.demo.entity.User;
import com.example.demo.jwt.JwtUtil;
import com.example.demo.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  private final UserRepository userRepository;
  private final JwtUtil jwtUtil;

  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest request, HttpServletResponse response, Authentication authentication)
      throws IOException, ServletException {
    OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
    String email = oAuth2User.getAttribute("email");
    String provider =
        oAuth2User.getAuthorities().stream().findFirst().get().getAuthority().contains("google")
            ? "google"
            : "naver";
    String providerId =
        oAuth2User.getAttribute("id") != null
            ? Objects.requireNonNull(oAuth2User.getAttribute("id")).toString()
            : oAuth2User.getAttribute("sub");

    User user =
        userRepository
            .findByEmail(email)
            .orElseGet(
                () -> {
                  User newUser =
                      User.builder()
                          .username(email)
                          .email(email)
                          .provider(provider)
                          .providerId(providerId)
                          .build();
                  return userRepository.save(newUser);
                });

    String token = jwtUtil.generateToken(user.getUsername());
    String redirectUrl = "/api/auth/oauth2/success?token=" + token;
    getRedirectStrategy().sendRedirect(request, response, redirectUrl);
  }
}
