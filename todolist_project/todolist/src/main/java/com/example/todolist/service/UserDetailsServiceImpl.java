package com.example.todolist.service;

import com.example.todolist.domain.AppUser;
import com.example.todolist.domain.AppUserRepository;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static org.springframework.security.core.userdetails.User.withUsername;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AppUserRepository appUserRepository;

    public UserDetailsServiceImpl(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Optional<> 을 사용하면 null 대신 Optional<null>과 유사한 형태가 return 되기 때문에 NullPointerException이 발생하지 않음 -> 그러면 예외처리 로직을 안 써도 된다.
        Optional<AppUser> user = appUserRepository.findByUsername(username);

        // 스프링 시큐리티(Spring Security)에서 온 클래스
        UserBuilder builder = null;
        if (user.isPresent()) {
            AppUser currentUser = user.get(); // Optional<AppUser>에서 실제 AppUser 꺼냄
            builder = withUsername(username); // username으로 UserBuilder 시작 / withUsername: username이 미리 설정된 UserBuilder 반환
            builder
                    .password(currentUser.getPassword()) // DB에 저장된(이미 암호화된) 비번 세팅 / 인코딩된 패스워드 그대로 전달
                    .roles(currentUser.getRole());        // 권한 세팅("USER"/"ADMIN" 등; ROLE_ 자동붙음) / 이미 "ROLE_*" 인 문자열을 그대로 권한으로 사용
        } else {
            throw new UsernameNotFoundException("User Not Found.");
        }
        return builder.build(); // UserDetails 객체 생성 및 반환
    }
}
