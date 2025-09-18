package org.com.newsletter.auth.domain.repository;

import java.time.Duration;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

/**
 * 리프레시 토큰 저장소 - Redis(String) 기반
 */
@Repository
@RequiredArgsConstructor
public class RefreshTokenRepository {
    private final StringRedisTemplate redisTemplate;
    private static final String PREFIX = "auth:refresh:";

    public void save(Long memberId, String token, Duration ttl) {
        String key = PREFIX + memberId;
        redisTemplate.opsForValue().set(key, token, ttl);
    }

    public Optional<String> find(Long memberId) {
        String value = redisTemplate.opsForValue().get(PREFIX + memberId);
        return Optional.ofNullable(value);
    }

    public void delete(Long memberId) {
        redisTemplate.delete(PREFIX + memberId);
    }

}
