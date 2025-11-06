package com.kw.Ddareungi.api.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.kw.Ddareungi.common.exception.BaseCode;
import com.kw.Ddareungi.common.exception.SuccessStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"status", "statusCode", "message", "count", "data"})
public class ApiResponseDto<T> {
    private final String status; // "success" | "error"
    private final Integer statusCode; // HTTP status code
    private final String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final Integer count; // optional

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final T data;

    // ===== Success factory methods =====
    public static <T> ApiResponseDto<T> onSuccess(T data) {
        HttpStatus httpStatus = SuccessStatus._SUCCESS.getHttpStatus();
        return new ApiResponseDto<>(
                "success",
                httpStatus.value(),
                SuccessStatus._SUCCESS.getMessage(),
                null,
                data
        );
    }

    public static <T> ApiResponseDto<T> onSuccess(T data, int count) {
        HttpStatus httpStatus = SuccessStatus._SUCCESS.getHttpStatus();
        return new ApiResponseDto<>(
                "success",
                httpStatus.value(),
                SuccessStatus._SUCCESS.getMessage(),
                count,
                data
        );
    }

    public static <T> ApiResponseDto<T> of(BaseCode code, T data) {
        HttpStatus httpStatus = code.getReasonHttpStatus().getHttpStatus();
        return new ApiResponseDto<>(
                "success",
                httpStatus != null ? httpStatus.value() : HttpStatus.OK.value(),
                code.getReasonHttpStatus().getMessage(),
                null,
                data
        );
    }

    // ===== Error factory methods =====
    public static <T> ApiResponseDto<T> onFailure(Integer statusCode, String message, T data) {
        return new ApiResponseDto<>(
                "error",
                statusCode,
                message,
                null,
                data
        );
    }

    public static <T> ApiResponseDto<T> onFailure(Integer statusCode, String message) {
        return new ApiResponseDto<>(
                "error",
                statusCode,
                message,
                null,
                null
        );
    }
}
