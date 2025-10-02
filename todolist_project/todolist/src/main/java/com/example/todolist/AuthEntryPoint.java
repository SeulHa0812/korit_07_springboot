package com.example.todolist;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;

// 로그인 실패 -> 401 띄운다.
// 알아서 확인해서 처리해줌. 어딘가에서 호출 x
@Component
// 인증이 안 된 요청이 보호된 API에 들어오면 무슨 응답을 줄지
public class AuthEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);       // HTTP 401 설정
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);     // 본문 타입: application/json (문자셋은 미지정)
            PrintWriter writer = response.getWriter();                     // 응답 바디에 쓸 Writer 획득 (postman 에서 에러메세지가 출력될 수 있도록)
            writer.println("Error : " + authException.getMessage());       // 에러 메시지 출력(지금은 JSON 형식 아님)
    }
}
