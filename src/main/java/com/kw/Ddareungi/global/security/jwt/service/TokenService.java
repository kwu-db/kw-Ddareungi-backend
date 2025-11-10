package com.kw.Ddareungi.global.security.jwt.service;


import com.kw.Ddareungi.global.security.dto.UserLoginRequestDto;
import com.kw.Ddareungi.global.security.jwt.dto.JwtToken;
import com.kw.Ddareungi.global.security.jwt.dto.MemberLoginRequestDto;
import org.springframework.security.core.Authentication;

import java.util.Date;

public interface TokenService {

    JwtToken login(UserLoginRequestDto userLoginRequestDto);
    JwtToken issueTokens(String refreshToken);

    JwtToken generateToken(Authentication authentication);

    Authentication getAuthentication(String accessToken);

    boolean validateToken(String token);

    boolean logout(String refreshToken);

    boolean existsRefreshToken(String refreshToken);

    Date parseExpiration(String token);

}