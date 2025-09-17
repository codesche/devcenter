package org.com.authproject.auth.dto.response;

import lombok.Builder;
import lombok.Getter;

/** 액세스/리프레시 토큰 응답 DTO */
@Getter
public class TokenResponse {
    private final String accessToken;
    private final String refreshToken;

    @Builder
    public TokenResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
