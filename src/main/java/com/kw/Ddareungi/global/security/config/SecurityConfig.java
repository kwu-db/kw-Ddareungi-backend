package com.kw.Ddareungi.global.security.config;


import com.kw.Ddareungi.global.security.exception.JwtAccessDeniedHandler;
import com.kw.Ddareungi.global.security.exception.JwtAuthenticationEntryPoint;
import com.kw.Ddareungi.global.security.filter.JwtAuthenticationFilter;
import com.kw.Ddareungi.global.security.filter.JwtExceptionFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;


@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtExceptionFilter jwtExceptionFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        configureCorsAndSecurity(httpSecurity);
        configureAuth(httpSecurity);
//        configureOAuth2(httpSecurity);
        configureExceptionHandling(httpSecurity);
        addFilter(httpSecurity);

        return httpSecurity.build();
    }

    private void configureCorsAndSecurity(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .headers(
                        httpSecurityHeadersConfigurer ->
                                httpSecurityHeadersConfigurer.frameOptions(
                                        HeadersConfigurer.FrameOptionsConfig::disable
                                )
                )
                // stateless한 rest api 이므로 csrf 공격 옵션 비활성화
                .csrf(AbstractHttpConfigurer::disable)
                //.formLogin(Customizer.withDefaults())
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(HttpBasicConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(configurer -> configurer
                        .sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                );
    }

    private void configureAuth(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .authorizeHttpRequests(Customizer.withDefaults())
                .authorizeHttpRequests(authorizeRequest -> {
                    authorizeRequest
                            .requestMatchers("/ws/**", "/subscribe/**", "/publish/**").permitAll()
                            .requestMatchers("/", "/.well-known/**", "/css/**", "/*.ico", "/error", "/images/**").permitAll()
                            .requestMatchers("/api/login", "/api/signup", "/api/health").permitAll()
                            .requestMatchers(HttpMethod.GET, permitAllGetPaths()).permitAll() // [GET] 인증 없이 접근 가능한 공개 API 경로
                            .requestMatchers(HttpMethod.POST, permitAllPostPaths()).permitAll() // [POST] 인증 없이 접근 가능한 공개 API 경로
                            .requestMatchers(HttpMethod.DELETE, permitAllDeletePaths()).permitAll()
                            .requestMatchers(swaggerPermitAllPaths()).permitAll()
                            .requestMatchers(authPermitAllPaths()).permitAll()
//                            .requestMatchers(permitAllRequestV2()).permitAll()
                            .anyRequest().authenticated();  // 그 외 모든 요청은 인증 필요
                });
    }

    //[GET] 인증 없이 접근 허용할 경로 목록
    private String[] permitAllGetPaths() {
        return new String[]{
                "/api/v1/examples/**",
                "/api/v1/test/**",
                "/api/v1/test/health-check",
                "/api/v1/examples/user",
                "/api/v1/examples/global",
                "/api/v1/tokens/**",
                "/api/v1/term",
                "/api/v1/stations",
                "/api/v1/stations/**",
                "/api/v1/passes",
                "/api/v1/boards",
                "/api/v1/boards/**",
                "/api/v1/comments/boards/**",
                "/actuator/**"
        };
    }

    //[POST] 인증 없이 접근 허용할 경로 목록
    private String[] permitAllPostPaths() {
        return new String[]{
                "/api/v1/tokens/**",
                "/api/v1/users",
                "/api/v1/experts",
                "/api/v1/images",
                "/api/v1/modification-request",
                "/api/v1/dummy/**"
        };
    }
    //[DELETE] 인증 없이 접근 허용할 경로 목록
    private String[] permitAllDeletePaths() {
        return new String[]{
                "/api/v1/experts/{expertId}/by-admin",
        };
    }


    private void addFilter(HttpSecurity httpSecurity) {
        httpSecurity
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtExceptionFilter, JwtAuthenticationFilter.class);
    }

    private void configureExceptionHandling(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .exceptionHandling(httpSecurityExceptionHandlingConfigurer -> httpSecurityExceptionHandlingConfigurer
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler));        // 403
    }

    private String[] swaggerPermitAllPaths() {
        return new String[]{
                "/swagger-ui/**",
                "/swagger-ui",
                "/swagger-ui.html",
                "/swagger/**",
                "/swagger-resources/**",
                "/v3/api-docs/**",
                "/profile"
        };
    }

    private String[] authPermitAllPaths() {
        return new String[]{
                "/oauth2/**",
                "/login/**",
                "/auth/**"
        };
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList(
                "http://localhost:5173",
                "http://localhost:3000",
                "https://local.tosel.co.kr",
                "https://klasplus.netlify.app"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }
}
