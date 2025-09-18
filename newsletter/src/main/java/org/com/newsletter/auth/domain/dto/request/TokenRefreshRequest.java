package org.com.newsletter.auth.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenRefreshRequest {
    @NotBlank(message = "refreshToken은 필수입니다.")
    private final String refreshToken;
}
