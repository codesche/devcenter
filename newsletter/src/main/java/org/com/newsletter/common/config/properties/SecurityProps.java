package org.com.newsletter.common.config.properties;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * JWT 보안 관련 프로퍼티.
 * - secret/TTL은 환경변수/시크릿 매니저에서 주입하기
 */
@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "security.jwt")
@Component
public class SecurityProps {

    @NotBlank
    private String secret;

    @Min(60)
    @Value("${security.jwt.access-token-ttl-seconds}")
    private long accessTtlSeconds;

    @Min(60)
    @Value("${security.jwt.refresh-token-ttl-seconds}")
    private long refreshTtlSeconds;

}
