package com.kw.Ddareungi.domain.user.service;

import com.kw.Ddareungi.domain.user.entity.User;
import com.kw.Ddareungi.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class UserCommandServiceImpl implements UserCommandService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Long createUser(String name, String email, String password) {
        LocalDateTime now = LocalDateTime.now();
        User user = User.builder()
                .username(email) // 임시로 이메일을 username으로 사용
                .name(name)
                .email(email)
                .password(passwordEncoder.encode(password))
                .createdDate(now)
                .lastModifiedDate(now)
                .build();
        return userRepository.save(user);
    }
}
