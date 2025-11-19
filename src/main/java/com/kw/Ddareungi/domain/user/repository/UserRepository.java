package com.kw.Ddareungi.domain.user.repository;

import com.kw.Ddareungi.domain.user.entity.User;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findById(Long id);
    Optional<User> findByUsername(String username);
    Optional<User> findByLoginId(String loginId);
    Long save(User user);
}
