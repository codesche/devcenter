package org.com.todolist.global.api;

import java.time.Instant;
import java.util.Map;
import lombok.Getter;

/**
 * API 응답 공통 포맷.
 */
@Getter
public class ApiResponse<T> {

    private boolean success;
    private T data;
    private String message;
    private Instant timestamp;

    private ApiResponse(boolean success, T data, String message) {
        this.success = success;
        this.data = data;
        this.message = message;
        this.timestamp = Instant.now();
    }

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, data, null);
    }

    public static <T> ApiResponse<T> ok(T data, String message) {
        return new ApiResponse<>(true, data, message);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, null, message);
    }

    // 필요 시 메타데이터를 위한 확장 생성자
    public static <T> ApiResponse<T> error(String message, Map<String, Object> meta) {
        return error(message);
    }

}
