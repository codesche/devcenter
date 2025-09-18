package org.com.newsletter.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.com.newsletter.auth.domain.dto.request.AuthLoginRequest;
import org.com.newsletter.auth.domain.dto.request.AuthRegisterRequest;
import org.com.newsletter.auth.domain.dto.response.AuthLoginResponse;
import org.com.newsletter.auth.domain.dto.response.TokenRefreshResponse;
import org.com.newsletter.auth.security.CustomUserDetails;
import org.com.newsletter.auth.service.AuthService;
import org.com.newsletter.common.response.RsData;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 인증 컨트롤러 - 검증/위임만 담당(비즈니스 로직 없음)
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<RsData<AuthLoginResponse>> register(@Valid @RequestBody
        AuthRegisterRequest req) {
        AuthLoginResponse res = authService.register(req);
        return ResponseEntity.ok(RsData.success("회원가입 완료!", res));
    }

    @PostMapping("/login")
    public ResponseEntity<RsData<AuthLoginResponse>> login(@Valid @RequestBody AuthLoginRequest req) {
        AuthLoginResponse res = authService.login(req);
        return ResponseEntity.ok(RsData.success("로그인 완료!", res));
    }

    @PostMapping("/refresh")
    public ResponseEntity<RsData<TokenRefreshResponse>> refresh(@AuthenticationPrincipal CustomUserDetails member) {
        TokenRefreshResponse res = authService.refresh(member.getId());
        return ResponseEntity.ok(RsData.success("refreshToken 발급", res));
    }

}
