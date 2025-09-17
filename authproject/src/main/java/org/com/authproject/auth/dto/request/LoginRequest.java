package org.com.authproject.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

/** 로그인 요청 DTO */
@Getter
public class LoginRequest {

    @NotBlank
    @Size(min = 4, max = 50)
    private final String username;

    @NotBlank
    @Size(min = 8, max = 64)
    private final String password;

    @Builder
    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

}
