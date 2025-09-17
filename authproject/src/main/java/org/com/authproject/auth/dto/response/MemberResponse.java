package org.com.authproject.auth.dto.response;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import org.com.authproject.auth.domain.Member;

/** 회원 정보 응답 DTO */
@Getter
public class MemberResponse {

    private final UUID id;
    private final String username;
    private final String nickname;

    @Builder
    private MemberResponse(UUID id, String username, String nickname) {
        this.id = id;
        this.username = username;
        this.nickname = nickname;
    }

    public static MemberResponse from(Member member) {
        return MemberResponse.builder()
            .id(member.getId())
            .username(member.getUsername())
            .nickname(member.getNickname())
            .build();
    }

}
