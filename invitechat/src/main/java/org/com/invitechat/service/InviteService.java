package org.com.invitechat.service;

import com.fasterxml.jackson.annotation.ObjectIdGenerators.UUIDGenerator;
import java.net.URI;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.com.invitechat.dto.response.InviteResponse;
import org.com.invitechat.entity.InviteToken;
import org.com.invitechat.redis.SimpleRateLimiter;
import org.com.invitechat.repository.InviteTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * 비즈니스 로직:
 *  - 생성 시 레이트 리미트 검사
 *  - UUID 기반 토큰 생성(충돌 가능성 극히 낮음)
 *  - 리다이렉트 URL 구성 및 검증
 */

@Service
@RequiredArgsConstructor
public class InviteService {

    private final InviteTokenRepository inviteTokenRepository;
    private final SimpleRateLimiter limiter;

    @Value("${app.frontend.base-url:https://app.example.com}")
    private String frontendBaseUrl;

    @Transactional
    public InviteResponse create(String roomId, String memberId, int ttlSeconds) {
        // 1) 생성 요청 레이트 리미트
        limiter.checkLimit("invite:create:" + memberId, 5);

        String token = generateUniqueToken();
        Instant expiresAt = Instant.now().plusSeconds(ttlSeconds);

        InviteToken saved = inviteTokenRepository.save(InviteToken.builder()
                .id(UUID.randomUUID().toString())
                .token(token)
                .roomId(roomId)
                .createdByMemberId(memberId)
                .expiresAt(expiresAt)
                .consumed(false)
                .build());

        return InviteResponse.builder()
            .token(token)
            .inviteUrl(frontendBaseUrl + "/inv/" + token)
            .expiresAt(saved.getExpiresAt())
            .roomId(roomId)
            .build();
    }

    public URI resolveRedirect(String token) {
        InviteToken it = inviteTokenRepository.findByToken(token)
            .filter(t -> !t.isConsumed())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (it.getExpiresAt().isBefore(Instant.now())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        // 선택: 1회성 소모 처리
        // it.setConsumed(true); repo.save(it);

        return URI.create(frontendJoinUrl(it.getRoomId(), token));
    }

    public InviteResponse getMeta(String roomId, String token) {
        InviteToken it = inviteTokenRepository.findByToken(token)
            .filter(t -> t.getRoomId().equals(roomId))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return InviteResponse.builder()
            .token(token)
            .inviteUrl(frontendBaseUrl + "/inv/" + it.getToken())
            .expiresAt(it.getExpiresAt())
            .roomId(it.getRoomId())
            .build();
    }

    @Transactional
    public void revoke(String roomId, String token) {
        InviteToken it = inviteTokenRepository.findByToken(token)
            .filter(t -> t.getRoomId().equals(roomId))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        inviteTokenRepository.delete(it);
    }

    private String frontendJoinUrl(String roomId, String token) {
        return frontendBaseUrl + "/#/rooms/" + roomId + "/join?token=" + token;
    }

    private String generateUniqueToken() {
        String candidate;
        do {
            candidate = UUID.randomUUID().toString();
        } while (inviteTokenRepository.existsByToken(candidate));
        return candidate;
    }

}
