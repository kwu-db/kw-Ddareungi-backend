package com.kw.Ddareungi.domain.user.service;

import com.kw.Ddareungi.domain.user.entity.User;
import com.kw.Ddareungi.domain.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.servers.Server;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class UserCommandServiceImpl implements UserCommandService {

    private final UserRepository userRepository;

    /**
     * SQL 입력
     * 1. argument에 맞게 USER 생성
     * @param name
     * @param email
     * @param password
     * @return
     */
    @Override
    public Long createUser(String name, String email, String password) {
        User user = User.builder()
                .name(name)
                .email(email)
                .password(password)
                .build();
        return userRepository.save(user).getId();
    }
}
