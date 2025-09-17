package org.com.authproject.auth.infra;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Getter
@RedisHash("refresh_tokens")
public class RefreshToken {

    @Id
    private String memberId;
    private String token;

    @TimeToLive
    private Long ttlSeconds;

    @Builder
    public RefreshToken(UUID memberId, String token, long ttlSeconds) {
        this.memberId = memberId.toString();
        this.token = token;
        this.ttlSeconds = ttlSeconds;
    }

    public static RefreshToken of(UUID memberId, String token, long ttlSeconds) {
        return RefreshToken.builder()
            .memberId(memberId)
            .token(token)
            .ttlSeconds(ttlSeconds)
            .build();
    }

}
