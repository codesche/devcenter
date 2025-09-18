package org.com.newsletter.auth.domain.dto.response;

import lombok.Builder;
import lombok.Getter;

/**
 * 로그인/회원가입 응답 DTO - 액세스/리프레시 토큰 반환.
 */
@Getter
@Builder
public class AuthLoginResponse {

    private final String accessToken;
    private final String refreshToken;

}
