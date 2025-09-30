package com.example.cardatabase_4.service;

import com.example.cardatabase_4.domain.AppUser;
import com.example.cardatabase_4.domain.AppUserRepository;
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

    @Override // 인증 처리 위해 강제 오버라이드 해야함 -> findByUsername() 오류남 -> 인터페이스에 자동 메서드 생성해줌(빨간글씨 눌러서)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<AppUser> user = appUserRepository.findByUsername(username);

        UserBuilder builder = null;
        if(user.isPresent()) {
            AppUser currentUser = user.get(); // user 자체는 Optional 자료형 AppUser가 아님.
            builder = withUsername(username);
            builder.password(currentUser.getPassword()).roles(currentUser.getRole()).build();
        } else {
            throw new UsernameNotFoundException("User Not Found.");
        }

        return builder.build();
    }
}
