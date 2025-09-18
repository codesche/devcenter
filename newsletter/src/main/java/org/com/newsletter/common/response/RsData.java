package org.com.newsletter.common.response;

import java.time.Instant;
import lombok.Builder;
import lombok.Getter;

/**
 * 공통 API 응답 래퍼.
 * - 성공/에러 응답의 일관성을 유지
 * - 시간 타입은 Instant 사용.
 */

@Getter
@Builder
public class RsData<T> {

    private final String code;          // 예: S-200, E-400
    private final String message;
    private final T data;
    private final Instant timestamp;    // 응답 시각(UTC)

    public static <T> RsData<T> success(String message, T data) {
        return RsData.<T>builder()
            .code("S-200")
            .message(message)
            .data(data)
            .timestamp(Instant.now())
            .build();
    }

    public static <T> RsData<T> error(String code, String message) {
        return RsData.<T>builder()
            .code(code)
            .message(message)
            .timestamp(Instant.now())
            .build();
    }

}
