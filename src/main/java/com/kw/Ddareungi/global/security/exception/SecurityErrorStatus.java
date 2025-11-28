package com.kw.Ddareungi.global.security.exception;


import com.kw.Ddareungi.global.annotation.ExplainError;
import com.kw.Ddareungi.global.exception.BaseErrorCode;
import com.kw.Ddareungi.global.exception.Reason;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.lang.reflect.Field;
import java.util.Objects;

@Getter
@AllArgsConstructor
public enum SecurityErrorStatus implements BaseErrorCode {

    //인증 관련 오류(4200~4249)
    AUTH_INVALID_TOKEN(HttpStatus.BAD_REQUEST, 4350, "유효하지 않은 토큰입니다."),
    AUTH_INVALID_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, 4351, "유효하지 않은 리프레시 토큰입니다."),
    AUTH_TOKEN_HAS_EXPIRED(HttpStatus.BAD_REQUEST, 4352, "토큰의 유효기간이 만료되었습니다."),
    AUTH_TOKEN_IS_UNSUPPORTED(HttpStatus.BAD_REQUEST, 4353, "서버에서 지원하지 않는 토큰 형식입니다."),
    AUTH_IS_NULL(HttpStatus.BAD_REQUEST, 4354, "토큰 값이 존재하지 않습니다."),
    AUTH_OAUTH2_EMAIL_NOT_FOUND_FROM_PROVIDER(HttpStatus.NOT_FOUND, 4355, "카카오 이메일이 존재하지 않습니다."),
    AUTH_MUST_AUTHORIZED_URI(HttpStatus.BAD_REQUEST, 4356, "인증이 필요한 URI입니다."),
    AUTH_ROLE_CANNOT_EXECUTE_URI(HttpStatus.BAD_REQUEST, 4357, "해당 권한으로는 요청을 처리할 수 없습니다."),
    AUTH_UNAUTHORIZED_LOGIN_DATA_RETRIEVAL_ERROR(HttpStatus.BAD_REQUEST, 4358, "로그인이 필요없는 API입니다."),
    AUTH_ASSIGNABLE_PARAMETER(HttpStatus.BAD_REQUEST, 4359, "인증타입이 잘못되어 할당이 불가능합니다."),
    AUTH_INVALID_ROLE(HttpStatus.FORBIDDEN, 4360, "유효하지 않은 역할(Role)입니다."),
    AUTH_WRONG_PASSWORD(HttpStatus.BAD_REQUEST, 4361, "패스워드가 잘못되었습니다." );

    private final HttpStatus httpStatus;
    private final Integer code;
    private final String message;

    @Override
    public Reason getReason() {
        return Reason.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .build();
    }

    @Override
    public Reason getReasonHttpStatus() {
        return Reason.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build();
    }

    @Override
    public String getExplainError() throws NoSuchFieldException {
        Field field = this.getClass().getField(this.name());
        ExplainError annotation = field.getAnnotation(ExplainError.class);
        return Objects.nonNull(annotation) ? annotation.value() : this.getMessage();
    }

}
