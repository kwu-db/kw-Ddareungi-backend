package com.kw.Ddareungi.global.security.controller;


import com.kw.Ddareungi.api.common.dto.ApiResponseDto;
import com.kw.Ddareungi.global.security.dto.UserLoginRequestDto;
import com.kw.Ddareungi.global.security.jwt.dto.JwtToken;
import com.kw.Ddareungi.global.security.jwt.dto.MemberLoginRequestDto;
import com.kw.Ddareungi.global.security.jwt.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "Token API", description = "JWT 토큰 관련 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/tokens")
public class TokenApiController {

    private final TokenService tokenService;

    @Operation(summary = "이메일로 JWT 토큰 발급")
    @PostMapping("/login")
    public ApiResponseDto<JwtToken> login(@RequestBody UserLoginRequestDto userLoginRequestDto) {
        return ApiResponseDto.onSuccess(tokenService.login(userLoginRequestDto));
    }

    @Operation(summary = "토큰 재발급", description = "Refresh Token으로 Access Token 재발급합니다.")
    @PostMapping("/reissue")
    public ApiResponseDto<JwtToken> issueToken(@RequestParam String refresh) {
        return ApiResponseDto.onSuccess(tokenService.issueTokens(refresh));
    }

//    @Operation(summary = "id unique 검증",
//    description = "중복은 true, 중복이 아니면 false를 리턴합니다.")
//    @GetMapping("/duplication/login-id")
//    public ApiResponseDto<Boolean> idValidator(@RequestParam String loginId) {
//        return ApiResponseDto.onSuccess(memberQueryService.validateExistLoginId(loginId));
//    }
//
//    @Operation(summary = "nickname unique 검증",
//    description = "중복은 true, 중복이 아니면 false를 리턴합니다.")
//    @GetMapping("/duplication/nickname")
//    public ApiResponseDto<Boolean> nicknameValidator(@RequestParam String nickname) {
//        return ApiResponseDto.onSuccess(memberQueryService.validateExistNickname(nickname));
//    }
}