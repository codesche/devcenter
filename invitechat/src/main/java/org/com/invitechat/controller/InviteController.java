package org.com.invitechat.controller;

import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.com.invitechat.dto.request.CreateInviteRequest;
import org.com.invitechat.dto.response.InviteResponse;
import org.com.invitechat.security.CustomUserPrincipal;
import org.com.invitechat.service.InviteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * 초대 링크 API
 * - 생성(레이트 리미트), 리다이렉트, 조회, 삭제
 */
@RestController
@RequiredArgsConstructor
public class InviteController {

    private final InviteService inviteService;

    /**
     * 초대 링크 생성
     * Authentication에서 CustomUserPrincipal을 캐스팅해 memberId를 사용한다.
     */
    @PostMapping("/api/rooms/{roomId}/invites")
    public InviteResponse create(@PathVariable String roomId,
                                @Valid @RequestBody CreateInviteRequest createInviteRequest,
                                Authentication auth) {
        String memberId = ((CustomUserPrincipal) auth.getPrincipal()).getId();
        return inviteService.create(roomId, memberId, createInviteRequest.getTtlSeconds());
    }

    /**
     * 초대 링크 사용(프론트로 302 리다이렉트)
     */
    @GetMapping("/inv/{token}")
    public ResponseEntity<Void> redirect(@PathVariable String token) {
        URI to = inviteService.resolveRedirect(token);
        return ResponseEntity.status(HttpStatus.FOUND).location(to).build();
    }

    /**
     * 메타 조회(관리용)
     */
    @GetMapping("/api/rooms/{roomId}/invites/{token}")
    public InviteResponse meta(@PathVariable String roomId, @PathVariable String token) {
        return inviteService.getMeta(roomId, token);
    }

    /**
     * 무효화(관리/방장 권한 체크는 Security 단에서 처리 가정)
     */
    @DeleteMapping("/api/rooms/{roomId}/invites/{token}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void revoke(@PathVariable String roomId, @PathVariable String token) {
        inviteService.revoke(roomId, token);
    }

}
