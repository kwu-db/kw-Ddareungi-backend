package com.kw.Ddareungi.domain.user.service;

import com.kw.Ddareungi.domain.user.dto.AdminRequestDto;
import com.kw.Ddareungi.domain.user.entity.Role;
import com.kw.Ddareungi.domain.user.entity.User;
import com.kw.Ddareungi.domain.user.repository.UserRepository;
import com.kw.Ddareungi.global.exception.ErrorStatus;
import com.kw.Ddareungi.global.exception.GeneralException;
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
				.role(Role.USER)
				.createdDate(now)
				.lastModifiedDate(now)
				.build();
		return userRepository.save(user);
	}

	@Override
	public Long createAdmin(AdminRequestDto adminRequestDto) {
		// 이메일 중복 체크
		if (userRepository.existsByEmail(adminRequestDto.getEmail())) {
			throw new GeneralException(ErrorStatus.ADMIN_ALREADY_EXISTS);
		}

		LocalDateTime now = LocalDateTime.now();
		User admin = User.builder()
				.username(adminRequestDto.getEmail())  // email을 username으로 사용
				.name(adminRequestDto.getName())
				.email(adminRequestDto.getEmail())
				.password(passwordEncoder.encode(adminRequestDto.getPassword()))
				.role(Role.ADMIN)
				.createdDate(now)
				.lastModifiedDate(now)
				.build();

		return userRepository.save(admin);
	}

	@Override
	public void updateAdmin(Long adminId, AdminRequestDto adminRequestDto) {
		// ADMIN 존재 확인
		User admin = userRepository.findByIdAndRole(adminId, Role.ADMIN)
				.orElseThrow(() -> new GeneralException(ErrorStatus.ADMIN_NOT_FOUND));

		// 이메일이 변경되는 경우 중복 체크
		if (!admin.getEmail().equals(adminRequestDto.getEmail())
				&& userRepository.existsByEmailExcludingId(adminRequestDto.getEmail(), adminId)) {
			throw new GeneralException(ErrorStatus.ADMIN_ALREADY_EXISTS);
		}

		// 선택적 업데이트
		int updatedRows = userRepository.updateAdminSelectively(
				adminId,
				adminRequestDto.getName(),
				adminRequestDto.getEmail(),
				adminRequestDto.getPassword() != null ? passwordEncoder.encode(adminRequestDto.getPassword()) : null
		);

		if (updatedRows == 0) {
			throw new GeneralException(ErrorStatus.ADMIN_NOT_FOUND);
		}
	}
}
