package org.com.newsletter.auth.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.newsletter.common.config.properties.SecurityProps;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final SecurityProps props;

    private Key key() {
        return Keys.hmacShaKeyFor(props.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    public String createAccessToken(Long memberId, String role) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(props.getAccessTtlSeconds());
        return Jwts.builder()
            .setSubject(String.valueOf(memberId))
            .claim("role", role)
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(exp))
            .signWith(key(), SignatureAlgorithm.ES256)
            .compact();
    }

    public Jws<Claims> parse(String token) {
        return Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token);
    }

}
