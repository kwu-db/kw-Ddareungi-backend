package com.kw.Ddareungi.global.security.jwt.service;

import com.kw.Ddareungi.domain.user.entity.User;
import com.kw.Ddareungi.domain.user.service.UserQueryService;
import com.kw.Ddareungi.global.exception.GeneralException;
import com.kw.Ddareungi.global.security.dto.UserLoginRequestDto;
import com.kw.Ddareungi.global.security.exception.JwtAuthenticationException;
import com.kw.Ddareungi.global.security.exception.JwtAuthenticationExpiredException;
import com.kw.Ddareungi.global.security.exception.SecurityErrorStatus;
import com.kw.Ddareungi.global.security.jwt.dto.JwtToken;
import com.kw.Ddareungi.global.service.RedisService;
import com.kw.Ddareungi.global.util.StaticVariable;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TokenServiceImpl implements TokenService{
    private final Key key;      //security yml 파일 생성 후 app.jwt.secret에 값 넣어주기(보안을 위해 따로 연락주세요)
    private final RedisService redisService;
    private final UserQueryService userQueryService;
    private final PasswordEncoder passwordEncoder;

    public TokenServiceImpl(@Value("${app.jwt.secret}") String key,
                            RedisService redisService,
                            PasswordEncoder passwordEncoder,
                            UserQueryService userQueryService) {
        byte[] keyBytes = Decoders.BASE64.decode(key);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.redisService = redisService;
        this.userQueryService = userQueryService;
        this.passwordEncoder = passwordEncoder;
    }
    @Override
    public JwtToken login(UserLoginRequestDto userLoginRequestDto) {
        User user = userQueryService.getByLoginId(userLoginRequestDto.getLoginId());
        if (!passwordEncoder.matches(userLoginRequestDto.getPassword(), user.getPassword())) {
            throw JwtAuthenticationException.WRONG_PASSWORD;
        }
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, "",
                user.getAuthorities());
        return generateToken(authentication);
    }

    @Override
    public JwtToken issueTokens(String refreshToken) {
        // Refresh Token 유효성 검사
        if (!validateToken(refreshToken) || !existsRefreshToken(refreshToken)) {
            throw new GeneralException(SecurityErrorStatus.AUTH_INVALID_REFRESH_TOKEN);
        }

        // 이전 리프레시 토큰 삭제
        redisService.deleteValue(refreshToken);

        // 새로운 Authentication 객체 생성
        Claims claims = parseClaims(refreshToken);
        String username = claims.getSubject();
        User user = userQueryService.getUserByUsername(username);
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, "",
                user.getAuthorities());

        // 새 토큰 생성
        JwtToken newTokens = generateToken(authentication);

        return newTokens;
    }

    @Override
    public JwtToken generateToken(Authentication authentication) {
        // 권한 가져오기
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();
        Date accessTokenExpiresIn = new Date(now + StaticVariable.ACCESS_TOKEN_EXPIRE_TIME);

        log.info("date = {}", accessTokenExpiresIn);
        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())
                .claim("auth", authorities)
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        // Refresh Token 생성
        String refreshToken = Jwts.builder()
                .setSubject(authentication.getName())
                .setExpiration(new Date(now + StaticVariable.REFRESH_TOKEN_EXPIRE_TIME))    // 7일
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        // 새 리프레시 토큰을 Redis에 저장
        redisService.setValue(refreshToken, authentication.getName());

        return JwtToken.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpire(parseExpiration(accessToken))
                .refreshTokenExpire(parseExpiration(refreshToken))
                .role(authentication.getAuthorities().toString())
                .build();
    }

    @Override
    public Authentication getAuthentication(String accessToken) {
        // Jwt 토큰 복호화
        Claims claims = parseClaims(accessToken);

        if (claims.get("auth") == null) {
            throw new JwtAuthenticationException(SecurityErrorStatus.AUTH_INVALID_TOKEN);
        }

        // 클레임에서 권한 정보 가져오기
        String authString = claims.get("auth").toString();
        Collection<? extends GrantedAuthority> authorities = Arrays.stream(authString.split(","))
                .filter(auth -> auth != null && !auth.trim().isEmpty())
                .map(String::trim)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        // UserDetails 객체를 만들어서 Authentication return
        // UserDetails: interface, User: UserDetails를 구현한 class
        // claims.getSubject()는 이메일 주소이므로, 이를 username으로 사용
        String username = claims.getSubject();
        UserDetails principal = new org.springframework.security.core.userdetails.User(username, "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
            throw JwtAuthenticationException.INVALID_TOKEN;
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
            throw JwtAuthenticationExpiredException.EXPIRED;
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
            throw JwtAuthenticationException.TOKEN_IS_UNSUPPORTED;
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
            throw JwtAuthenticationException.AUTH_NULL;
        }
    }

    @Override
    public boolean logout(String refreshToken) {
        redisService.deleteValue(refreshToken);
        return true;
    }

    @Override
    public boolean existsRefreshToken(String refreshToken) {
        return redisService.getValue(refreshToken) != null;
    }

    @Override
    public Date parseExpiration(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return claims.getExpiration();
        } catch (JwtException exception) {
            throw new JwtAuthenticationException(SecurityErrorStatus.AUTH_INVALID_TOKEN);
        }
    }

    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}

