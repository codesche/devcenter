package org.com.authproject.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

/** 회원가입 요청 DTO */
@Getter
public class SignupRequest {

    @NotBlank
    @Size(min = 4, max = 50)
    private final String username;

    @NotBlank
    @Size(min = 8, max = 64)
    private final String password;

    @NotBlank
    @Size(min = 2, max = 50)
    private final String nickname;

    @Builder
    public SignupRequest(String username, String password, String nickname) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
    }

}
