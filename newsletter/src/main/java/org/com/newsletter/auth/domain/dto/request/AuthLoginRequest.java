package org.com.newsletter.auth.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

/**
 * 로그인 요청 DTO
 */
@Getter
@Builder
public class AuthLoginRequest {

    @NotBlank(message = "username은 필수입니다.")
    private final String username;

    @NotBlank(message = "password는 필수입니다.")
    private final String password;

}
