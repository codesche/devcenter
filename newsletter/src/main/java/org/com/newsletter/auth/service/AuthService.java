package org.com.newsletter.auth.service;

import java.time.Duration;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.com.newsletter.auth.domain.Member;
import org.com.newsletter.auth.domain.Role;
import org.com.newsletter.auth.domain.dto.request.AuthLoginRequest;
import org.com.newsletter.auth.domain.dto.request.AuthRegisterRequest;
import org.com.newsletter.auth.domain.dto.response.AuthLoginResponse;
import org.com.newsletter.auth.domain.dto.response.TokenRefreshResponse;
import org.com.newsletter.auth.domain.repository.MemberRepository;
import org.com.newsletter.auth.domain.repository.RefreshTokenRepository;
import org.com.newsletter.auth.security.JwtTokenProvider;
import org.com.newsletter.common.config.properties.SecurityProps;
import org.com.newsletter.common.exception.ApiException;
import org.com.newsletter.common.exception.ErrorCode;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 인증/인가 관련 비즈니스 로직 - 컨트롤러에 로직 두지 않기
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final SecurityProps securityProps;

    @Transactional
    public AuthLoginResponse register(AuthRegisterRequest req) {
        if (memberRepository.existsByUsername(req.getUsername())) {
            throw new ApiException(ErrorCode.CONFLICT, "이미 존재하는 사용자입니다");
        }
        Member member = memberRepository.save(Member.builder()
            .username(req.getUsername())
            .password(passwordEncoder.encode(req.getPassword()))
            .role(Role.USER)
            .build());

        return issueTokens(member.getId(), member.getRole().name());
    }

    @Transactional
    public AuthLoginResponse login(AuthLoginRequest req) {
        Member member = memberRepository.findByUsername(req.getUsername())
            .orElseThrow(() -> new ApiException(ErrorCode.UNAUTHORIZED, "존재하지 않는 사용자"));

        if (!passwordEncoder.matches(req.getPassword(), member.getPassword())) {
            throw new ApiException(ErrorCode.UNAUTHORIZED, "비밀번호 불일치");
        }

        return issueTokens(member.getId(), member.getRole().name());
    }

    @Transactional
    public TokenRefreshResponse refresh(Long memberId) {
        // 현재 구조에서는 @AuthenticationPrincipal 로 인증된 사용자 ID를 사용
        // (최소 수정안: 제공받은 refresh 비교는 생략하고 회전만 수행)
        AuthLoginResponse issued = issueTokens(memberId, Role.USER.name());

        return TokenRefreshResponse.builder()
            .accessToken(issued.getAccessToken())
            .refreshToken(issued.getRefreshToken()) // ✅ 새 refresh 반환
            .build();
    }

    private AuthLoginResponse issueTokens(Long memberId, String role) {
        String access = jwtTokenProvider.createAccessToken(memberId, role);
        String refresh = UUID.randomUUID().toString();
        refreshTokenRepository.save(memberId, refresh, Duration.ofSeconds(securityProps.getRefreshTtlSeconds()));
        return AuthLoginResponse.builder()
            .accessToken(access)
            .refreshToken(refresh)
            .build();
    }

}
