package org.com.authproject.auth.infra.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import org.com.authproject.auth.config.JwtProperties;
import org.springframework.stereotype.Component;

/** JWT 발급/검증 유틸리티 */
@Component
public class JwtProvider {

    private final Key key;
    private final long accessTtl;
    private final long refreshTtl;

    public JwtProvider(JwtProperties props) {
        this.key = Keys.hmacShaKeyFor(props.getSecret().getBytes(StandardCharsets.UTF_8));
        this.accessTtl = props.getAccessTokenSeconds();
        this.refreshTtl = props.getRefreshTokenSeconds();
    }

    public String createAccessToken(String subject, Map<String, Object> claims) {
        return createToken(subject, claims, accessTtl);
    }

    public String createRefreshToken(String subject) {
        return createToken(subject, Map.of("type", "refresh"), refreshTtl);
    }

    public Jws<Claims> parse(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
    }

    public boolean isExpired(String token) {
        try {
            Date exp = parse(token).getBody().getExpiration();
            return exp.before(new Date());
        } catch (JwtException e) {
            return true;
        }
    }

    private String createToken(String subject, Map<String, Object> claims, long ttlSeconds) {
        Instant now = Instant.now();
        Date iat = Date.from(now);
        Date exp = Date.from(now.plusSeconds(ttlSeconds));
        return Jwts.builder()
            .setSubject(subject)
            .addClaims(claims)
            .setIssuedAt(iat)
            .setExpiration(exp)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
    }

}
