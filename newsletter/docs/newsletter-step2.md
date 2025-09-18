# 📑 Project 4 — Step 2: 인증 모듈 구현

## ✅ 목표

* 회원가입/로그인/토큰 재발급 기능 구현
* AccessToken: JWT 기반, 짧은 TTL
* RefreshToken: UUID + Redis 기반, 긴 TTL (회전 시 새 토큰 교체)
* 전역 예외 처리 + 공통 응답(RsData) 일관 적용
* 컨트롤러는 검증/위임만 담당, 비즈니스 로직은 서비스 계층 전담

---

## 📂 구현 경로

```
src/main/java/com/example/newscache/auth
├─ domain
│   ├─ Member.java
│   └─ Role.java
├─ repository
│   ├─ MemberRepository.java
│   ├─ RefreshTokenRepository.java
│   
├─ dto
│   ├─ AuthRegisterRequest.java
│   ├─ AuthLoginRequest.java
│   ├─ AuthLoginResponse.java
│   ├─ TokenRefreshResponse.java
├─ security
│   ├─ CustomUserDetails.java
│   ├─ CustomUserDetailsService.java
│   └─ JwtAuthenticationFilter.java
|    └─ JwtTokenProvider.java
├─ service
│   └─ AuthService.java
└─ controller
    └─ AuthController.java
```

---

## 🧱 주요 코드 요약

### Member / Role

* JPA 엔티티, `BaseTime` 상속
* username 고유 인덱스 적용

```java
@Entity
@Table(name = "members", indexes = {
    @Index(name = "idx_member_username", columnList = "username", unique = true)
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Member extends BaseTime {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;
}
```

### JwtTokenProvider

* AccessToken(JWT) 생성/파싱만 담당
* RefreshToken은 UUID + Redis 관리 → JWT 아님

```java
public String createAccessToken(Long memberId, String role) {
    Instant now = Instant.now();
    Instant exp = now.plusSeconds(props.getAccessTtlSeconds());
    return Jwts.builder()
        .setSubject(String.valueOf(memberId))
        .claim("role", role)
        .setIssuedAt(Date.from(now))
        .setExpiration(Date.from(exp))
        .signWith(key(), SignatureAlgorithm.HS256)
        .compact();
}
```

### RefreshTokenRepository

* Redis 기반 저장/조회/삭제

```java
@Repository
@RequiredArgsConstructor
public class RefreshTokenRepository {
    private final StringRedisTemplate redis;
    private static final String PREFIX = "auth:refresh:";

    public void save(Long memberId, String token, Duration ttl) {
        redis.opsForValue().set(PREFIX + memberId, token, ttl);
    }
    public Optional<String> find(Long memberId) { ... }
    public void delete(Long memberId) { ... }
}
```

### AuthService

* 회원가입: 중복 체크 → 패스워드 암호화 → Member 저장 → 토큰 발급
* 로그인: 사용자 검증/비밀번호 매칭 → 토큰 발급
* 토큰 재발급: Redis에서 refreshToken 확인 → 새 Access + 새 Refresh 발급 → Redis 갱신 → 응답 반환

```java
@Transactional
public TokenRefreshResponse refresh(Long memberId) {
    // 최소 수정안: @AuthenticationPrincipal 기반 memberId 사용
    AuthLoginResponse issued = issueTokens(memberId, Role.USER.name());

    return TokenRefreshResponse.builder()
        .accessToken(issued.getAccessToken())
        .refreshToken(issued.getRefreshToken()) // ✅ 새 refresh 반환
        .build();
}
```

### DTO: TokenRefreshResponse

* 기존 accessToken만 반환 → refreshToken 필드 추가

```java
@Getter
@Builder
public class TokenRefreshResponse {
    private final String accessToken;
    private final String refreshToken; // ✅ 추가
}
```

### AuthController

* 컨트롤러는 검증/위임만 수행
* `/refresh` 엔드포인트는 @AuthenticationPrincipal 로 memberId 추출 (최소 수정안 유지)

```java
@PostMapping("/refresh")
public ResponseEntity<RsData<TokenRefreshResponse>> refresh(
        @AuthenticationPrincipal CustomUserDetails user) {
    TokenRefreshResponse res = authService.refresh(user.getId());
    return ResponseEntity.ok(RsData.success("REFRESHED", res));
}
```

---

## 🔑 핵심 변화 (Step 2)

1. RefreshToken 관리 전략 명확화: **UUID + Redis TTL**
2. `TokenRefreshResponse`에 `refreshToken` 필드 추가
3. `AuthService.refresh()`에서 Access + Refresh 모두 반환
4. Controller는 그대로 유지, 반환 DTO만 확장

---

## 📌 체크리스트

* [x] 회원가입/로그인/재발급 기능 동작
* [x] RefreshToken 회전 시 클라이언트와 서버 불일치 문제 해결
* [x] 전역 예외/공통 응답 일관 적용

---

## 📌 다음 단계

👉 Step 3: **외부 뉴스 API(WebClient) + DTO 매핑 + 캐싱 서비스 연결**
