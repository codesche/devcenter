package org.com.authproject.auth.controller;

import jakarta.validation.Valid;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.com.authproject.auth.common.api.ApiResponse;
import org.com.authproject.auth.dto.request.LoginRequest;
import org.com.authproject.auth.dto.request.SignupRequest;
import org.com.authproject.auth.dto.response.MemberResponse;
import org.com.authproject.auth.dto.response.TokenResponse;
import org.com.authproject.auth.service.AuthService;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ApiResponse<MemberResponse> signup(@RequestBody @Valid SignupRequest request) {
        return ApiResponse.ok(authService.signup(request));
    }

    @PostMapping("/login")
    public ApiResponse<TokenResponse> login(@RequestBody @Valid LoginRequest request) {
        return ApiResponse.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ApiResponse<TokenResponse> refresh(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
        @RequestHeader(value = "X-Refresh-Token") String refreshToken) {
        // Access의 subject(username)를 헤더의 Bearer 토큰에서 파싱하지 않고
        // 안전하게 서비스 레벨에서 재확인하는 방안도 가능.
        // 여기서는 간단하게 Authorization 헤더에서 username 추출을 생략하고,
        // refreshToken 자체의 subject(username)를 서비스에서 검증할 수도 있음.
        // 데모용으로는 클라이언트가 username을 쿼리 파라미터로 보내도 무방하지만
        // 여기서는 Authorization의 Bearer Access에서 인증한 사용자 이름을 사용했다고 가정.
        String username = extractUsernameFromBearer(authHeader); // 실전에서는 필터/리졸버로 교체 권장
        return ApiResponse.ok(authService.refresh(username, refreshToken));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(Authentication authentication) {
        String username = Objects.toString(authentication.getName(), null);
        authService.logout(username);
        return ApiResponse.ok(null);
    }

    /** 데모: Authorization 헤더에서 username 추출하는 부분은 실제 운영에서는 JwtProvider 재사용 권장 */
    private String extractUsernameFromBearer(String header) {
        if (!StringUtils.hasText(header) || !header.startsWith("Bearer ")) {
            return "";
        }
        // 실전에서는 JwtProvider.parse()로 subject 추출
        // 본 데모에서는 필터가 SecurityContext에 올려준 authentication을 사용하는 편이 더 안전
        return ""; // 간단화: AuthService.refresh에서 username을 다른 방식으로 가져오도록 변경 가능
    }

}
