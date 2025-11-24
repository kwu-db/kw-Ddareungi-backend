package com.kw.Ddareungi.domain.user.service;

import com.kw.Ddareungi.domain.user.dto.AdminRequestDto;

public interface UserCommandService {

	Long createUser(String name, String email, String password);

	Long createAdmin(AdminRequestDto adminRequestDto);

	void updateAdmin(Long adminId, AdminRequestDto adminRequestDto);
}
