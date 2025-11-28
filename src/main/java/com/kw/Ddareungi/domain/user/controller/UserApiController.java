package com.kw.Ddareungi.domain.user.controller;

import com.kw.Ddareungi.api.common.dto.ApiResponseDto;
import com.kw.Ddareungi.domain.user.dto.UserRequestDto;
import com.kw.Ddareungi.domain.user.service.UserCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "유저 API")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserApiController {

    private final UserCommandService userCommandService;

    @Operation(summary = "사용자 생성", description = "이름, 이메일, 비밀번호를 입력받아 새로운 사용자를 생성합니다.")
    @PostMapping
    public ResponseEntity<ApiResponseDto<Long>> createUser(@Valid @RequestBody UserRequestDto.CreateUser request) {
        Long userId = userCommandService.createUser(request.getName(), request.getEmail(), request.getPassword());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDto.onSuccess(userId));
    }
}
