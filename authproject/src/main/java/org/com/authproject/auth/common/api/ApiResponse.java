package org.com.authproject.auth.common.api;

import lombok.Builder;
import lombok.Getter;

/**
 * 공통 API 응답 포맷
 * - 성공/실패를 일관된 형태로 전달
 */
@Getter
public class ApiResponse<T> {

    private final boolean success;
    private final String message;
    private final T data;

    @Builder
    private ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponse<T> ok(T data) {
        return ApiResponse.<T> builder()
            .success(true)
            .message("OK")
            .data(data)
            .build();
    }

    public static <T> ApiResponse<T> fail(String message) {
        return ApiResponse.<T>builder()
            .success(false)
            .message(message)
            .data(null)
            .build();
    }

}
