package com.kw.Ddareungi.domain.user.dto;

import com.kw.Ddareungi.domain.user.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminResponseDto {
	private Long userId;
	private String name;
	private String email;
	private Role role;
	private LocalDateTime createdDate;
	private LocalDateTime lastModifiedDate;
}

