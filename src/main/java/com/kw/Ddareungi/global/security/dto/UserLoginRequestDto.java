package com.kw.Ddareungi.global.security.dto;

import lombok.*;

@Builder
@Data
public class UserLoginRequestDto {
    private String loginId;
    private String password;
}
