package org.com.invitechat.dto.response;

import java.time.Instant;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InviteResponse {

    private String token;
    private String inviteUrl;           // 프론트용 단축 링크(/inv/{token})
    private Instant expiresAt;
    private String roomId;

}
