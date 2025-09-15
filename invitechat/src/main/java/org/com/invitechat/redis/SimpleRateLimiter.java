package org.com.invitechat.redis;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

/**
 * 분 단위 카운터 기반 레이트 리미팅:
 *  - 키 규칙: prefix:{memberId}:{yyyyMMddHHmm}
 *  - INCR 후 최초 1이면 EXPIRE 60s 설정
 *  - 임계치 초과 시 429(Too Many Requests)
 */

@Component
@RequiredArgsConstructor
public class SimpleRateLimiter {

    private final StringRedisTemplate redis;

    public void checkLimit(String key, int limitPerMin) {
        String minuteKey = key + ":" + DateTimeFormatter.ofPattern("yyyyMMddHHmm")
            .withZone(ZoneId.of("UTC")).format(Instant.now());

        Long count = redis.opsForValue().increment(minuteKey);
        if (count != null && count == 1L) {
            redis.expire(minuteKey, Duration.ofMinutes(1));
        }

        if (count != null && count > limitPerMin) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Rate limit exceeded");
        }
    }

}
