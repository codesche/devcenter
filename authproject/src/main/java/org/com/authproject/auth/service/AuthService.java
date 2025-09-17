package org.com.authproject.auth.service;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.com.authproject.auth.common.exception.DomainException;
import org.com.authproject.auth.common.exception.ErrorCode;
import org.com.authproject.auth.domain.Member;
import org.com.authproject.auth.dto.request.LoginRequest;
import org.com.authproject.auth.dto.request.SignupRequest;
import org.com.authproject.auth.dto.response.MemberResponse;
import org.com.authproject.auth.dto.response.TokenResponse;
import org.com.authproject.auth.infra.RefreshToken;
import org.com.authproject.auth.infra.RefreshTokenRepository;
import org.com.authproject.auth.infra.jwt.JwtProvider;
import org.com.authproject.auth.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 인증/인가 핵심 비즈니스 로직
 * - 회원가입, 로그인, 토큰 재발급, 로그아웃
 * - 엔티티를 파라미터로 받지 않음!
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh-token-seconds}")
    private long ttlSeconds;

    @Transactional
    public MemberResponse signup(SignupRequest req) {
        if (memberRepository.existsByUsername(req.getUsername())) {
            throw new DomainException(ErrorCode.MEMBER_DUPLICATED);
        }

        String hash = passwordEncoder.encode(req.getPassword());
        Member member = Member.builder()
            .username(req.getUsername())
            .passwordHash(hash)
            .nickname(req.getNickname())
            .build();

        Member saved = memberRepository.save(member);
        return MemberResponse.from(saved);
    }

    @Transactional
    public TokenResponse login(LoginRequest req) {
        Member member = memberRepository.findByUsername(req.getUsername())
            .orElseThrow(() -> new DomainException(ErrorCode.AUTH_INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(req.getPassword(), member.getPasswordHash())) {
            throw new DomainException(ErrorCode.AUTH_INVALID_CREDENTIALS);
        }

        // Access/Refresh 발급
        String access = jwtProvider.createAccessToken(member.getUsername(),
            Map.of("uid", member.getId().toString()));

        String refresh = jwtProvider.createRefreshToken(member.getUsername());

        // 멤버별 단일 RefreshToken 저장(회전 기준점)
        refreshTokenRepository.save(RefreshToken.of(member.getId(), refresh, ttlSeconds));

        return TokenResponse.builder()
            .accessToken(access)
            .refreshToken(refresh)
            .build();
    }

    @Transactional
    public TokenResponse refresh(String username, String requestRefreshToken) {
        Member member = memberRepository.findByUsername(username)
            .orElseThrow(() -> new DomainException(ErrorCode.MEMBER_NOT_FOUND));

        // 저장된 RefreshToken 조회
        RefreshToken saved = refreshTokenRepository.findById(member.getId().toString())
            .orElseThrow(() -> new DomainException(ErrorCode.AUTH_REFRESH_NOT_FOUND));

        // 토큰 일치 + 만료 확인
        if (!saved.getToken().equals(requestRefreshToken) || jwtProvider.isExpired(requestRefreshToken)) {
            throw new DomainException(ErrorCode.AUTH_REFRESH_MISMATCH);
        }

        // 기존 Refresh 교체
        String newAccess = jwtProvider.createAccessToken(member.getUsername(), Map.of("uid", member.getId().toString()));
        String newRefresh = jwtProvider.createRefreshToken(member.getUsername());
        refreshTokenRepository.save(RefreshToken.of(member.getId(), newRefresh, ttlSeconds));

        return TokenResponse.builder()
            .accessToken(newAccess)
            .refreshToken(newRefresh)
            .build();
    }

    @Transactional
    public void logout(String username) {
        memberRepository.findByUsername(username)
            .ifPresent(member -> refreshTokenRepository.deleteById(member.getId().toString()));
    }
}















