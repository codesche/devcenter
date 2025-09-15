# Mini Assignment: Invite Token API (Spring Boot)

## 개요
이 문서는 TRPG 채팅 프로젝트를 기반으로 한 **일회성 초대 링크(Invite Token)** 기능 구현 예시를 정리한 것이다.  
실무 과제 테스트 및 협업 면접 대비를 위한 작은 단위의 기능이지만, TTL, Rate Limiting, 계층 분리 등 실무 포인트가 포함되어 있다.

---

## 기능 요구사항
1. **초대 링크 생성**
   - `POST /api/rooms/{roomId}/invites`
   - TTL(만료시간) 지정 가능 (기본 30분)
   - 동일 사용자는 **분당 5회**까지만 생성 가능 → 초과 시 `429 Too Many Requests`
   - 응답:
     ```json
     { "token": "UUIDv7", "inviteUrl": "https://app.example.com/inv/{token}", "expiresAt": "..." }
     ```

2. **초대 링크 사용**
   - `GET /inv/{token}`
   - 유효하면 **302 Redirect** → 프론트엔드 `/#/rooms/{roomId}/join?token=...`
   - 만료·삭제·무효화된 토큰이면 `404 Not Found`

3. **관리/조회**
   - `GET /api/rooms/{roomId}/invites/{token}`: 메타데이터 조회
   - `DELETE /api/rooms/{roomId}/invites/{token}`: 무효화 (방장/관리자 권한 필요)

---

## 데이터 모델

```java
@Document(collection = "invite_tokens")
@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
public class InviteToken {
    @Id 
    private String id; // UUIDv7

    @Indexed(unique = true)
    private String token; // 초대 토큰(현재 UUID 사용)

    @Indexed
    private String roomId; // 초대 대상 채팅방 ID

    private String createdByMemberId; // 생성한 사용자 ID

    @Indexed(expireAfterSeconds = 0)
    private Instant expiresAt; // TTL 인덱스 → MongoDB가 만료시 자동 삭제

    private boolean consumed; // 1회성 사용 여부 (false 기본값)
}
```

---

## Repository

```java
public interface InviteTokenRepository extends MongoRepository<InviteToken, String> {
    Optional<InviteToken> findByToken(String token);
    boolean existsByToken(String token);
}
```

---

## DTO

```java
@Getter 
@Setter
public class CreateInviteRequest {
    @Min(60) 
    @Max(86400)
    private Integer ttlSeconds = 1800; // 기본 30분
}

@Getter 
@Builder
public class InviteResponse {
    private String token;      // 토큰 문자열
    private String inviteUrl;  // 프론트에서 사용할 초대 URL
    private Instant expiresAt; // 만료 시간
    private String roomId;     // 채팅방 ID
}
```

---

## CustomUserPrincipal

```java
// Spring Security의 Authentication.getPrincipal()에서 사용되는 커스텀 사용자 객체
@Getter
@AllArgsConstructor
public class CustomUserPrincipal implements UserDetails {
    private final String id;       // Member 엔티티의 ID
    private final String username; // 로그인 계정명
    private final String password; // 암호화된 비밀번호
    private final Collection<? extends GrantedAuthority> authorities;

    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return true; }
}
```

---

## Rate Limiter (Redis)

```java
@Component
@RequiredArgsConstructor
public class SimpleRateLimiter {
    private final StringRedisTemplate redis;

    public void checkLimit(String key, int limitPerMin) {
        // 현재 분 단위 키 생성
        String minuteKey = key + ":" + DateTimeFormatter.ofPattern("yyyyMMddHHmm")
                .withZone(ZoneId.of("UTC"))
                .format(Instant.now());

        // 카운트 증가
        Long count = redis.opsForValue().increment(minuteKey);

        // 첫 생성 시 만료 시간 1분 설정
        if (count != null && count == 1L) {
            redis.expire(minuteKey, Duration.ofMinutes(1));
        }

        // 제한 초과 시 429 에러 발생
        if (count != null && count > limitPerMin) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Rate limit exceeded");
        }
    }
}
```

---

## Service

```java
@Service
@RequiredArgsConstructor
public class InviteService {
    private final InviteTokenRepository repo;
    private final SimpleRateLimiter limiter;

    @Value("${app.frontend.base-url:https://app.example.com}")
    private String frontendBaseUrl;

    @Transactional
    public InviteResponse create(String roomId, String memberId, int ttlSeconds) {
        // 분당 5회 제한 확인
        limiter.checkLimit("invite:create:" + memberId, 5);

        // UUID 기반 토큰 생성
        String token = UUID.randomUUID().toString();
        Instant expiresAt = Instant.now().plusSeconds(ttlSeconds);

        // 저장
        InviteToken saved = repo.save(InviteToken.builder()
                .id(UUID.randomUUID().toString())
                .token(token)
                .roomId(roomId)
                .createdByMemberId(memberId)
                .expiresAt(expiresAt)
                .consumed(false)
                .build());

        // 응답 DTO 반환
        return InviteResponse.builder()
                .token(token)
                .inviteUrl(frontendBaseUrl + "/inv/" + token)
                .expiresAt(saved.getExpiresAt())
                .roomId(roomId)
                .build();
    }

    public URI resolveRedirect(String token) {
        InviteToken it = repo.findByToken(token)
                .filter(t -> !t.isConsumed())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (it.getExpiresAt().isBefore(Instant.now())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        // 프론트엔드 채팅방 조인 URL 반환
        return URI.create(frontendBaseUrl + "/#/rooms/" + it.getRoomId() + "/join?token=" + token);
    }

    public InviteResponse getMeta(String roomId, String token) {
        InviteToken it = repo.findByToken(token)
                .filter(t -> t.getRoomId().equals(roomId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return InviteResponse.builder()
                .token(it.getToken())
                .inviteUrl(frontendBaseUrl + "/inv/" + it.getToken())
                .expiresAt(it.getExpiresAt())
                .roomId(it.getRoomId())
                .build();
    }

    @Transactional
    public void revoke(String roomId, String token) {
        InviteToken it = repo.findByToken(token)
                .filter(t -> t.getRoomId().equals(roomId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        repo.delete(it);
    }
}
```

---

## Controller

```java
@RestController
@RequiredArgsConstructor
public class InviteController {
    private final InviteService service;

    // 초대 링크 생성
    @PostMapping("/api/rooms/{roomId}/invites")
    public InviteResponse create(@PathVariable String roomId,
                                 @Valid @RequestBody CreateInviteRequest req,
                                 Authentication auth) {
        // CustomUserPrincipal에서 사용자 ID 추출
        String memberId = ((CustomUserPrincipal) auth.getPrincipal()).getId();
        return service.create(roomId, memberId, req.getTtlSeconds());
    }

    // 초대 링크 리다이렉트
    @GetMapping("/inv/{token}")
    public ResponseEntity<Void> redirect(@PathVariable String token) {
        URI to = service.resolveRedirect(token);
        return ResponseEntity.status(HttpStatus.FOUND).location(to).build();
    }

    // 초대 링크 메타 조회
    @GetMapping("/api/rooms/{roomId}/invites/{token}")
    public InviteResponse meta(@PathVariable String roomId, @PathVariable String token) {
        return service.getMeta(roomId, token);
    }

    // 초대 링크 무효화
    @DeleteMapping("/api/rooms/{roomId}/invites/{token}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void revoke(@PathVariable String roomId, @PathVariable String token) {
        service.revoke(roomId, token);
    }
}
```

---

## 면접 대응 포인트
- **TTL 인덱스**: MongoDB가 만료 데이터를 자동 삭제 → 운영 단순화  
- **Rate Limiting**: Redis 카운터로 구현 → abuse 방지  
- **DTO 반환**: Controller는 DTO만 반환, Entity는 노출하지 않음  
- **UUID 사용**: Base62 대신 UUID 사용해도 무방. 다만 URL 길이가 길어질 수 있어 단축성이 필요하면 Base62가 유리하다.  
- **계층 분리**: Controller → Service → Repository 구조로 책임 분리  
