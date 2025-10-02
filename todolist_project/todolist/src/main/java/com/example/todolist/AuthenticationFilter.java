package com.example.todolist;

import com.example.todolist.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

// OncePerRequestFilter -> abstract class

// 매 요청마다 실행되면서,
// Authorization 헤더에서 JWT를 꺼내 인증 객체(Authentication) 를 만들어 SecurityContext에 넣는 역할

// Jwt: 클레임을 담는 토큰 표준(헤더·페이로드·서명 구조).
// Jws: 그 토큰/데이터에 디지털 서명을 적용한 형태, 즉 ‘서명된 JWT’.
@Component
public class AuthenticationFilter extends OncePerRequestFilter {
    // 이 필터가 JWT를 해석/검증하고 username을 꺼내려면 JwtService가 필요
    private final JwtService jwtService;
    // 생성자 매개변수
    public AuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jws = request.getHeader(HttpHeaders.AUTHORIZATION); // Authorization 헤더 원문(예: "Bearer <JWT>") 읽기
        if (jws != null) { // 헤더가 있으면(유효성은 아직 확인 안 함)
            String user = jwtService.getAuthUser(request); // JwtService로 토큰 검증/파싱하여 subject(username) 추출
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    user,                 // principal: 인증 주체(여기서는 username 문자열)
                    null,                 // credentials: 비밀번호 등 자격 증명은 불필요하므로 null
                    Collections.emptyList() // authorities: 권한 목록 없음(역할 검사 필요 없을 때)
            ); // -> 이 한 줄로 “인증된 사용자” 객체 생성
            SecurityContextHolder.getContext().setAuthentication(authentication); // 현재 요청의 보안 컨텍스트에 인증 정보 저장
        }
        filterChain.doFilter(request, response); // 필수: 다음 필터/컨트롤러로 요청 계속 진행
    }
}
