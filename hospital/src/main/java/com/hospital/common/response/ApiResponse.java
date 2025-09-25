package com.hospital.common.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import lombok.Builder;
import lombok.Getter;

/**
 * 공통 API 응답 객체
 * 모든 API 응답은 이 형태로 통일하여 일관성 있는 응답 구조 제공
 * @param <T> 응답 데이터 타입
 */
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    /**
     * 응답 상태 (SUCCESS, ERROR)
     */
    private final ResponseStatus status;

    /**
     * 응답 메시지
     */
    private final String message;

    /**
     * 응답 데이터
     */
    private final T data;

    /**
     * 에러 코드 (에러 발생 시에만 포함)
     */
    private final String errorCode;

    /**
     * 응답 생성 시간 (UTC 기준)
     */
    @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    @Builder.Default
    private final Instant timestamp = Instant.now();

    /**
     * 성공 응답 생성 (데이터 포함)
     * @param message 성공 메시지
     * @param data    응답 데이터
     * @param <T>     데이터 타입
     * @return 성공 응답 객체
     */
     public static <T> ApiResponse<T> success(String message, T data) {
         return ApiResponse.<T>builder()
             .status(ResponseStatus.SUCCESS)
             .message(message)
             .data(data)
             .build();
     }

    /**
     * 성공 응답 생성 (데이터 없음)
     * @param message 성공 메시지
     * @param <T>     데이터 타입
     * @return 성공 응답 객체
     */
    public static <T> ApiResponse<T> success(String message) {
        return ApiResponse.<T>builder()
            .status(ResponseStatus.SUCCESS)
            .message(message)
            .build();
    }

    /**
     * 에러 응답 생성 (에러 코드 포함)
     *
     * @param message   에러 메시지
     * @param errorCode 에러 코드
     * @param <T>       데이터 타입
     * @return 에러 응답 객체
     */
    public static <T> ApiResponse<T> error(String message, String errorCode) {
        return ApiResponse.<T>builder()
            .status(ResponseStatus.ERROR)
            .message(message)
            .errorCode(errorCode)
            .build();
    }

    /**
     * 에러 응답 생성 (에러 코드 없음)
     *
     * @param message 에러 메시지
     * @param <T>     데이터 타입
     * @return 에러 응답 객체
     */
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
            .status(ResponseStatus.ERROR)
            .message(message)
            .build();
    }

    /**
     * 기본 성공 응답 생성
     *
     * @param <T> 데이터 타입
     * @return 기본 성공 응답 객체
     */
    public static <T> ApiResponse<T> ok() {
        return ApiResponse.<T>builder()
            .status(ResponseStatus.SUCCESS)
            .message("성공")
            .build();
    }

    /**
     * 기본 성공 응답 생성 (데이터 포함)
     * @param data 응답 데이터
     * @param <T>  데이터 타입
     * @return 기본 성공 응답 객체
     */
    public static <T> ApiResponse<T> ok(T data) {
        return ApiResponse.<T>builder()
            .status(ResponseStatus.SUCCESS)
            .message("성공")
            .data(data)
            .build();
    }

    /**
     * 응답 성공 확인
     */
    public boolean isSuccess() {
        return ResponseStatus.SUCCESS.equals(this.status);
    }

    /**
     * 응답 에러 확인
     */
    public boolean isError() {
        return ResponseStatus.ERROR.equals(this.status);
    }
}
