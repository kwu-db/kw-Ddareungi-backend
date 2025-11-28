package com.kw.Ddareungi.domain.user.repository;

import com.kw.Ddareungi.domain.user.entity.Role;
import com.kw.Ddareungi.domain.user.entity.User;

import java.util.Optional;

public interface UserRepository {
	Optional<User> findById(Long id);
	Optional<User> findByUsername(String username);
	Optional<User> findByLoginId(String loginId);
	Long save(User user);
	boolean existsByEmail(String email);
	Optional<User> findByIdAndRole(Long id, Role role);
	boolean existsByEmailExcludingId(String email, Long id);
	int updateAdminSelectively(Long adminId, String name, String email, String password);
}
