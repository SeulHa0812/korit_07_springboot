package com.shoppinglist.shoppinglist2.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtService jwtService;

    // application.properties에서 설정한 리다이렉트 URL 주입
    @Value("${oauth2.success.redirect-url}")
    private String redirectUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // OAuth2 인증 성공 후 호출됨.
        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();

        // User 식별 정보 추출 (예를 들어 : email / sub)
        // Google 에서 email 또는 sub 속성이 사용 가능함.
        String username = oauthUser.getAttribute("email"); // sub로 써도됨.
        if(username == null) {
            logger.warn("OAuth2에서 username을 추출할 수 없습니다.");
            username = "oauth2user_" + oauthUser.getName();
        }

        // JWT 토큰 생성
        String token = jwtService.generateToken(username);

        // 프론트엔드로 리다이렉트할 URL 생성(토큰을 쿼리 파라미터로 추가해줘야 함)
        String targetUrl = UriComponentsBuilder.fromUriString(redirectUrl)
                .queryParam("token", token)
                .build()
                .encode(StandardCharsets.UTF_8) // UTF_8 인코딩 추가
                .toUriString();
        // 기존의 세션 제거
        clearAuthenticationAttributes(request);

        // 생성된 URL로 리다이렉트
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
