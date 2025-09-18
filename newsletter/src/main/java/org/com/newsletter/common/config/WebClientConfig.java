package org.com.newsletter.common.config;

import org.com.newsletter.common.config.properties.NewsProps;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * 외부 뉴스 API 호출용
 * - Authorization 헤더에 API 키를 주입한다.
 */
public class WebClientConfig {

    @Bean
    public WebClient newsClient(NewsProps props) {
        return WebClient.builder()
            .baseUrl(props.getBaseUrl())
            .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + props.getApiKey())
            .build();
    }

}
