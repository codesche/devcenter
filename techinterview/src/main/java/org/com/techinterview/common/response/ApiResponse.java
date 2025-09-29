package org.com.techinterview.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import lombok.Builder;
import lombok.Getter;

/**
 * 공통 API 응답 객체
 * 모든 API 응답은 이 형식을 따름
 */
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private final boolean success;
    private final T data;
    private final ErrorResponse error;
    private final Instant timestamp;

    // 성공 응답 생성
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
            .success(true)
            .data(data)
            .timestamp(Instant.now())
            .build();
    }

    // 에러 응답 생성
    public static ApiResponse<?> error(ErrorResponse error) {
        return ApiResponse.builder()
            .success(false)
            .error(error)
            .timestamp(Instant.now())
            .build();
    }


}
