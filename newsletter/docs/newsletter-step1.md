# 📑 Project 4 — Step 1: 공통 모듈 구현

## ✅ 목표

외부 뉴스 API/인증 등 본격 기능 개발에 앞서 **공통 모듈**을 먼저 구성.

* 전역 예외 처리
* 공통 API 응답 객체
* 시간 필드(BaseTime)
* Redis & Cache 설정
* 외부 API/WebClient 설정
* 프로퍼티 바인딩(News, Security)

---

## 📂 구현 경로

```
src/main/java/com/example/newscache/common
├─ response
│   └─ RsData.java
├─ exception
│   ├─ ErrorCode.java
│   ├─ ApiException.java
│   └─ GlobalExceptionHandler.java
├─ time
│   └─ BaseTime.java
├─ config
│   ├─ RedisConfig.java
│   ├─ CacheConfig.java
│   ├─ WebClientConfig.java
│   └─ properties
│       ├─ NewsProps.java
│       └─ SecurityProps.java
```

---

## 🧱 주요 코드 요약

### RsData (공통 응답)

* 성공/실패 응답을 일관성 있게 제공.
* `Instant` 기반 타임스탬프.

```java
@Getter
@Builder
public class RsData<T> {
    private final String code;
    private final String message;
    private final T data;
    private final Instant timestamp;

    public static <T> RsData<T> success(String message, T data) { ... }
    public static <T> RsData<T> error(String code, String message) { ... }
}
```

### GlobalExceptionHandler

* `ApiException` → code + message 변환.
* `MethodArgumentNotValidException`, `AccessDeniedException`, `Exception` 공통 처리.

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<RsData<Void>> handleApi(ApiException e) { ... }
}
```

### BaseTime

* 모든 엔티티에서 상속.
* `@PrePersist`, `@PreUpdate` 이벤트 리스너로 `Instant` 값 자동 주입.

```java
@MappedSuperclass
@Getter
public abstract class BaseTime {
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
    @Column(nullable = false)
    private Instant updatedAt;
}
```

### RedisConfig / CacheConfig

* Lettuce 기반 Redis 연결.
* 캐시 TTL 기본 5분.
* `news:search` 캐시 영역 분리.

```java
@Configuration
@EnableCaching
public class CacheConfig {
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory cf) { ... }
}
```

### WebClientConfig

* 외부 뉴스 API 호출용 WebClient Bean.
* Authorization 헤더에 API Key 자동 삽입.

```java
@Configuration
@EnableConfigurationProperties({NewsProps.class, SecurityProps.class})
public class WebClientConfig {
    @Bean
    public WebClient newsClient(NewsProps props) { ... }
}
```

### NewsProps / SecurityProps

* `@ConfigurationProperties` 기반 프로퍼티 바인딩.
* **주의**: API Key, JWT Secret 등은 환경변수/시크릿 매니저로 주입 (추후 적용).

```java
@ConfigurationProperties(prefix = "external.news")
public class NewsProps {
    private String baseUrl;
    private String apiKey;
    private int defaultPageSize;
}
```

```java
@ConfigurationProperties(prefix = "security.jwt")
public class SecurityProps {
    private String secret;
    private long accessTtlSeconds;
    private long refreshTtlSeconds;
}
```

---

## 📝 체크리스트

* [x] RsData / 전역 예외 / BaseTime 구성
* [x] Redis, CacheConfig 적용
* [x] WebClientConfig 및 프로퍼티 바인딩
* [x] 환경변수/시크릿 매니저 적용 계획 반영

---

## 📌 다음 단계

👉 Step 2: **인증(회원/레포/서비스/컨트롤러) + JWT/Redis Refresh** 구현
