package com.kw.Ddareungi.global.exception;

import com.kw.Ddareungi.global.annotation.ExplainError;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.lang.reflect.Field;
import java.util.Objects;

import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode{

    // 서버 오류
    @ExplainError("500번대 알수없는 오류입니다. 서버 관리자에게 문의 주세요")
    _INTERNAL_SERVER_ERROR(INTERNAL_SERVER_ERROR, 5000, "서버 에러, 관리자에게 문의 바랍니다."),
    @ExplainError("인증이 필요없는 api입니다.")
    _UNAUTHORIZED_LOGIN_DATA_RETRIEVAL_ERROR(INTERNAL_SERVER_ERROR, 5001, "서버 에러, 로그인이 필요없는 요청입니다."),
    _ASSIGNABLE_PARAMETER(BAD_REQUEST, 5002, "인증타입이 잘못되어 할당이 불가능합니다."),

    // 일반적인 요청 오류
    _BAD_REQUEST(BAD_REQUEST, 4000, "잘못된 요청입니다."),
    _UNAUTHORIZED(UNAUTHORIZED, 4001, "로그인이 필요합니다."),
    _FORBIDDEN(FORBIDDEN, 4002, "금지된 요청입니다."),

	// user (4050-4099)
	@ExplainError("사용자를 찾을 수 없습니다.")
	USER_NOT_FOUND(NOT_FOUND, 4050, "사용자를 찾을 수 없습니다."),

	// rental (4100-4149)
	// consultationRequest (4150-4199)
	// admin (4150-4199)
	@ExplainError("관리자를 찾을 수 없습니다.")
	ADMIN_NOT_FOUND(NOT_FOUND, 4150, "관리자를 찾을 수 없습니다."),
	@ExplainError("이미 존재하는 관리자입니다.")
	ADMIN_ALREADY_EXISTS(BAD_REQUEST, 4151, "이미 존재하는 관리자입니다."),

	// station (4200-4249)
	@ExplainError("대여소를 찾을 수 없습니다.")
	STATION_NOT_FOUND(NOT_FOUND, 4200, "대여소를 찾을 수 없습니다."),
	@ExplainError("이미 존재하는 대여소입니다.")
	STATION_ALREADY_EXISTS(BAD_REQUEST, 4201, "이미 존재하는 대여소입니다.");

    // expertNotification (4200-4249)
    // userNotification (4250-4299)
    // proposal (4300-4349)
    // 인증 관련 오류 (4350~4399)
    // image (4400-4449)
    // report (4450~4499)
    // advice (4500~4549)

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
