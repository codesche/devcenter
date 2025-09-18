package org.com.newsletter.auth.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 토큰 발급 DTO
 */
@Getter
@Builder
public class TokenRefreshResponse {
    private final String accessToken;
    private final String refreshToken;
}
