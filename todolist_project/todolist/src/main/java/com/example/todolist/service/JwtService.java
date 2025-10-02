package com.example.todolist.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service // 스프링 빈으로 등록: 다른 곳에서 주입받아 사용
public class JwtService {
    static final long EXPIRATION = 86400000;         // 토큰 유효기간(ms) = 24시간
    static final String PREFIX = "Bearer";           // Authorization 헤더의 접두어(일반적으로 "Bearer " 형태) // 콘솔에 Authorization : Bearer (토큰)
    static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256); // HS256에 맞는 비밀키 생성 (앱이 재시작될 때마다 키가 바뀜 → 이전에 발급했던 토큰은 전부 검증 실패(무효) 됨.)

    public String getToken(String username) {
        String token = Jwts.builder()                         // JWT 빌더 시작
                .setSubject(username)                         // sub 클레임: 토큰 주체(여기서는 username)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION)) // exp 클레임: 만료시각
                .signWith(key)                                // 서명(HS256용 키) → JWS 생성
                .compact();  // .build(); 대신                 // 직렬화(문자열)하여 최종 토큰 생성
        return token;                                         // "xxxxx.yyyyy.zzzzz" 형태의 문자열 반환
    }

//    public String getAuthUser(HttpServletRequest request) {
//        String token = request.getHeader(HttpHeaders.AUTHORIZATION);  // Authorization 헤더 읽기 -> postman 상에서 Headers -> Key-Authorization 추가 -> Value-생성된 토큰 Bearer 뒤 복붙
//        if (token != null) {
//            String user = Jwts.parser()                            // 파서 빌더(최신 권장: parserBuilder()) / 파서(문장의 구조 분석·오류 점검 프로그램).
//                    .setSigningKey(key)                               // 검증에 사용할 서명 키 설정
//                    .build()                                          // 파서 생성
//
//                    .parseClaimsJws(token.replace(PREFIX, "").trim()) // "Bearer" 접두어 제거 후 파싱/검증
//                    .getBody()                                        // Claims(페이로드) 추출
//                    .getSubject();                                    // sub(=username) 읽기
//            if (user != null) return user;                            // 사용자명 있으면 반환
//        }
//        return null;                                                  // 헤더 없거나 파싱 실패 시 null
//    }

    public String getAuthUser(HttpServletRequest request) {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (token != null) {
            String user = Jwts.parser()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJwt(token.replace(PREFIX, "").trim())
                    .getBody()
                    .getSubject();
            if (user != null) return user;
        }
        return null;
    }

}
