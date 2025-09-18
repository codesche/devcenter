package org.com.newsletter.common.config.properties;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * 외부 뉴스 API 설정 바인딩
 * - 실서비스 키/시크릿은 환경변수/시크릿 매니저에서 주입하기
 */
@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "external.news")
@Component
public class NewsProps {

    @NotBlank
    private String baseUrl;

    @NotBlank
    private String apiKey;

    @Min(1)
    private int defaultPageSize = 10;

}
