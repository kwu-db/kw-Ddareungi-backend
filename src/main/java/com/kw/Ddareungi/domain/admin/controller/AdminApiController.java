package com.kw.Ddareungi.domain.admin.controller;

import com.kw.Ddareungi.api.common.dto.ApiResponseDto;
import com.kw.Ddareungi.domain.user.dto.AdminRequestDto;
import com.kw.Ddareungi.domain.user.dto.AdminResponseDto;
import com.kw.Ddareungi.domain.user.entity.User;
import com.kw.Ddareungi.domain.user.service.UserCommandService;
import com.kw.Ddareungi.domain.user.service.UserQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "관리자 API")
@RestController
@RequestMapping("/api/v1/admins")
@RequiredArgsConstructor
public class AdminApiController {

	private final UserCommandService userCommandService;
	private final UserQueryService userQueryService;

	@Operation(summary = "관리자 계정 생성", description = "새로운 관리자 계정을 생성합니다.")
	@PostMapping
	public ApiResponseDto<Long> createAdmin(@Valid @RequestBody AdminRequestDto adminRequestDto) {
		Long adminId = userCommandService.createAdmin(adminRequestDto);
		return ApiResponseDto.onSuccess(adminId);
	}

	@Operation(summary = "관리자 정보 변경", description = "관리자 정보를 변경합니다.")
	@PatchMapping("/{adminId}")
	public ApiResponseDto<AdminResponseDto> updateAdmin(
			@Parameter(description = "관리자 ID", required = true) @PathVariable Long adminId,
			@Valid @RequestBody AdminRequestDto adminRequestDto) {
		userCommandService.updateAdmin(adminId, adminRequestDto);

		User admin = userQueryService.getUser(adminId);
		AdminResponseDto response = AdminResponseDto.builder()
				.userId(admin.getId())
				.name(admin.getName())
				.email(admin.getEmail())
				.role(admin.getRole())
				.createdDate(admin.getCreatedDate())
				.lastModifiedDate(admin.getLastModifiedDate())
				.build();

		return ApiResponseDto.onSuccess(response);
	}
}

