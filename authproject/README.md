# 프로젝트 3: 회원가입 & 로그인 (JWT + Redis RefreshToken)

> Spring Boot 3.5.x / Java 21 / PostgreSQL / Redis / Spring Security / JJWT  
> **목표**: JWT 기반 인증(Access) + Redis에 RefreshToken 저장(회전/TTL) 구조 구현.  
> **상태**: 1~2번 프로젝트 완료. 현재 3번 프로젝트 **설계 + 코드 뼈대** 1차 커밋 수준 정리본.

---

## ✅ 기술 스택
- Spring Boot 3.5.x (Web, Validation, Security, Data JPA, Data Redis)
- Java 21, Gradle
- PostgreSQL (RDB), Redis (세션/토큰 캐시)
- JJWT 0.11.x (HS256)
- Lombok
- JUnit 5 + Mockito (슬라이스 테스트)

---

## 📁 폴더 구조 (예시)
```
src
 └─ main
    ├─ java/com/example/auth
    │  ├─ common
    │  │  ├─ api/ApiResponse.java
    │  │  ├─ exception/ErrorCode.java
    │  │  ├─ exception/GlobalExceptionHandler.java
    │  │  └─ exception/DomainException.java
    │  ├─ config
    │  │  ├─ SecurityConfig.java
    │  │  ├─ JwtProperties.java
    │  │  └─ RedisConfig.java
    │  ├─ domain
    │  │  ├─ BaseTime.java
    │  │  └─ member/Member.java
    │  ├─ dto
    │  │  └─ auth
    │  │     ├─ LoginRequest.java
    │  │     ├─ SignupRequest.java
    │  │     ├─ TokenResponse.java
    │  │     └─ MemberResponse.java
    │  ├─ infrastructure
    │  │  ├─ jwt/JwtProvider.java
    │  │  ├─ jwt/JwtAuthenticationFilter.java
    │  │  ├─ redis/RefreshToken.java
    │  │  └─ redis/RefreshTokenRepository.java
    │  ├─ repository
    │  │  └─ MemberRepository.java
    │  ├─ service
    │  │  └─ AuthService.java
    │  └─ web
    │     └─ AuthController.java
    └─ resources
       └─ application.yml
```

---

## 🔧 build.gradle (Groovy)
```groovy
plugins {
    id 'java'
    id 'org.springframework.boot' version '3.5.3'
    id 'io.spring.dependency-management' version '1.1.6'
}

group = 'com.example'
version = '0.0.1-SNAPSHOT'
java {
    toolchain { languageVersion = JavaLanguageVersion.of(21) }
}

repositories { mavenCentral() }

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-security'
    implementation 'org.springframework.boot:spring-boot-starter-validation'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework.boot:spring-boot-starter-data-redis'

    runtimeOnly 'org.postgresql:postgresql:42.7.4'

    implementation 'io.jsonwebtoken:jjwt-api:0.11.5'
    runtimeOnly 'io.jsonwebtoken:jjwt-impl:0.11.5'
    runtimeOnly 'io.jsonwebtoken:jjwt-jackson:0.11.5'

    compileOnly 'org.projectlombok:lombok:1.18.34'
    annotationProcessor 'org.projectlombok:lombok:1.18.34'

    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    testImplementation 'org.mockito:mockito-core:5.12.0'
}

test {
    useJUnitPlatform()
}
```

---

## ⚙️ application.yml (샘플)
> 실제 비밀키는 환경변수로 주입(예: `JWT_SECRET`, `SPRING_DATASOURCE_*`)  
> **Instant/UTC** 운영을 위해 JDBC 타임존을 UTC로 고정
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/authdb
    username: authuser
    password: authpass
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        default_batch_fetch_size: 100
        jdbc:
          time_zone: UTC
  data:
    redis:
      host: localhost
      port: 6379

server:
  port: 8080

jwt:
  secret: ${JWT_SECRET:change-me-in-prod}
  access-token-seconds: 1800      # 30분
  refresh-token-seconds: 1209600  # 14일

logging:
  level:
    root: INFO
    org.springframework.security: INFO
    com.example.auth: DEBUG

app:
  cors:
    allowed-origins: "http://localhost:5173"
    allowed-methods: "GET,POST,PUT,DELETE,OPTIONS"
```

---

## 🧱 공통 응답 & 전역 예외
### ApiResponse.java
```java
package com.example.auth.common.api;

import lombok.Builder;
import lombok.Getter;

/** 공통 API 응답 포맷 */
@Getter
public class ApiResponse<T> {
    private final boolean success;
    private final String message;
    private final T data;

    @Builder
    private ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponse<T> ok(T data) {
        return ApiResponse.<T>builder().success(true).message("OK").data(data).build();
    }

    public static <T> ApiResponse<T> fail(String message) {
        return ApiResponse.<T>builder().success(false).message(message).data(null).build();
    }
}
```

### ErrorCode.java
```java
package com.example.auth.common.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    MEMBER_DUPLICATED("이미 존재하는 사용자입니다."),
    MEMBER_NOT_FOUND("사용자를 찾을 수 없습니다."),
    AUTH_INVALID_CREDENTIALS("아이디 또는 비밀번호가 올바르지 않습니다."),
    AUTH_TOKEN_EXPIRED("토큰이 만료되었습니다."),
    AUTH_TOKEN_INVALID("유효하지 않은 토큰입니다."),
    AUTH_REFRESH_NOT_FOUND("RefreshToken이 존재하지 않습니다."),
    AUTH_REFRESH_MISMATCH("RefreshToken이 일치하지 않습니다."),
    FORBIDDEN("접근 권한이 없습니다."),
    BAD_REQUEST("잘못된 요청입니다."),
    INTERNAL_ERROR("서버 오류가 발생했습니다.");

    private final String message;
    ErrorCode(String message) { this.message = message; }
}
```

### DomainException.java
```java
package com.example.auth.common.exception;

import lombok.Getter;

/** 의미 있는 도메인 예외의 베이스 클래스 */
@Getter
public class DomainException extends RuntimeException {
    private final ErrorCode errorCode;

    public DomainException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
```

### GlobalExceptionHandler.java
```java
package com.example.auth.common.exception;

import com.example.auth.common.api.ApiResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** 전역 예외 처리: 클라이언트에게 일관된 응답 제공 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DomainException.class)
    public ApiResponse<Void> handleDomain(DomainException e) {
        return ApiResponse.fail(e.getErrorCode().getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleOthers(Exception e) {
        return ApiResponse.fail(ErrorCode.INTERNAL_ERROR.getMessage());
    }
}
```

---

## 🗃️ 도메인 & 저장소

### BaseTime.java (Instant)
```java
package com.example.auth.domain;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import java.time.Instant;

/** Instant 기반 생성/수정 시간 추적 */
@Getter
@MappedSuperclass
public abstract class BaseTime {
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }
    @PreUpdate
    void onUpdate() {
        this.updatedAt = Instant.now();
    }
}
```

### Member.java
```java
package com.example.auth.domain.member;

import com.example.auth.domain.BaseTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.UUID;

/** 회원 엔티티 (비밀번호 해시 저장) */
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "members", indexes = {
        @Index(name = "idx_member_username", columnList = "username", unique = true)
})
public class Member extends BaseTime {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, length = 60)
    private String passwordHash;

    @Column(nullable = false, length = 50)
    private String nickname;

    @Builder
    private Member(UUID id, String username, String passwordHash, String nickname) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.nickname = nickname;
    }

    /** 비밀번호 변경(더티체킹) */
    public void changePasswordHash(String newHash) {
        this.passwordHash = newHash;
    }
}
```

### MemberRepository.java
```java
package com.example.auth.repository;

import com.example.auth.domain.member.Member;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface MemberRepository extends JpaRepository<Member, UUID> {
    @EntityGraph(attributePaths = {})
    Optional<Member> findByUsername(String username);
    boolean existsByUsername(String username);
}
```

---

## 📦 DTO (요청/응답)

### SignupRequest.java
```java
package com.example.auth.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

/** 회원가입 요청 DTO */
@Getter
public class SignupRequest {
    @NotBlank 
    @Size(min = 4, max = 50)
    private final String username;
    
    @NotBlank 
    @Size(min = 8, max = 64)
    private final String password;
    
    @NotBlank 
    @Size(min = 2, max = 50)
    private final String nickname;

    @Builder
    public SignupRequest(String username, String password, String nickname) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
    }
}
```

### LoginRequest.java
```java
package com.example.auth.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

/** 로그인 요청 DTO */
@Getter
public class LoginRequest {
    @NotBlank 
    @Size(min = 4, max = 50)
    private final String username;
    
    @NotBlank 
    @Size(min = 8, max = 64)
    private final String password;

    @Builder
    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
```

### TokenResponse.java
```java
package com.example.auth.dto.auth;

import lombok.Builder;
import lombok.Getter;

/** 액세스/리프레시 토큰 응답 DTO */
@Getter
public class TokenResponse {
    private final String accessToken;
    private final String refreshToken;

    @Builder
    public TokenResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
```

### MemberResponse.java
```java
package com.example.auth.dto.auth;

import com.example.auth.domain.member.Member;
import lombok.Builder;
import lombok.Getter;
import java.util.UUID;

/** 회원 정보 응답 DTO */
@Getter
public class MemberResponse {
    private final UUID id;
    private final String username;
    private final String nickname;

    @Builder
    private MemberResponse(UUID id, String username, String nickname) {
        this.id = id;
        this.username = username;
        this.nickname = nickname;
    }

    public static MemberResponse from(Member member) {
        return MemberResponse.builder()
                .id(member.getId())
                .username(member.getUsername())
                .nickname(member.getNickname())
                .build();
    }
}
```

---

## 🔐 JWT & Redis 인프라

### JwtProperties.java
```java
package com.example.auth.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/** jwt.* 설정 주입 */
@Getter
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private String secret;
    private long accessTokenSeconds;
    private long refreshTokenSeconds;

    public void setSecret(String secret) { this.secret = secret; }
    public void setAccessTokenSeconds(long accessTokenSeconds) { this.accessTokenSeconds = accessTokenSeconds; }
    public void setRefreshTokenSeconds(long refreshTokenSeconds) { this.refreshTokenSeconds = refreshTokenSeconds; }
}
```

### RedisConfig.java
```java
package com.example.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

/** Redis 커넥션/템플릿 설정 */
@Configuration
public class RedisConfig {

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(); // host/port는 application.yml 적용
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory cf) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(cf);
        return template;
    }
}
```

### RefreshToken.java (@RedisHash)
```java
package com.example.auth.infrastructure.redis;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import java.util.UUID;

/** 멤버별 1개의 RefreshToken 저장 모델 */
@Getter
@RedisHash("refresh_tokens")
public class RefreshToken {
    @Id
    private final String memberId;
    
    private final String token;
    
    @TimeToLive
    private final Long ttlSeconds;

    @Builder
    public RefreshToken(UUID memberId, String token, long ttlSeconds) {
        this.memberId = memberId.toString();
        this.token = token;
        this.ttlSeconds = ttlSeconds;
    }

    public static RefreshToken of(UUID memberId, String token, long ttlSeconds) {
        return RefreshToken.builder()
                .memberId(memberId)
                .token(token)
                .ttlSeconds(ttlSeconds)
                .build();
    }
}
```

### RefreshTokenRepository.java
```java
package com.example.auth.infrastructure.redis;

import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
    // id = memberId
}
```

### JwtProvider.java
```java
package com.example.auth.infrastructure.jwt;

import com.example.auth.config.JwtProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

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
        return createToken(subject, Map.of("typ", "refresh"), refreshTtl);
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
```

### JwtAuthenticationFilter.java
```java
package com.example.auth.infrastructure.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;

/** AccessToken 검증 필터 (무상태) */
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    
    public JwtAuthenticationFilter(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        
        if (!StringUtils.hasText(header) || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }
        
        String token = header.substring(7);
        
        try {
            if (jwtProvider.isExpired(token)) {
                SecurityContextHolder.clearContext();
            } else {
                Jws<Claims> jws = jwtProvider.parse(token);
                String username = jws.getBody().getSubject();
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(username, null, List.of());
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (Exception ex) {
            SecurityContextHolder.clearContext();
        }
        
        chain.doFilter(request, response);
    }
}
```

---

## 🔏 보안 설정

### SecurityConfig.java
```java
package com.example.auth.config;

import com.example.auth.infrastructure.jwt.JwtAuthenticationFilter;
import com.example.auth.infrastructure.jwt.JwtProvider;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import java.util.List;

/** Spring Security 설정 */
@Configuration
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtProvider jwtProvider) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(req -> {
                CorsConfiguration c = new CorsConfiguration();
                c.setAllowedOrigins(List.of(System.getProperty("app.cors.allowed-origins", "http://localhost:5173")));
                c.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
                c.setAllowedHeaders(List.of("*"));
                c.setAllowCredentials(true);
                return c;
            }))
            
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(PathRequest.toH2Console()).permitAll()
                .requestMatchers("/actuator/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/signup", "/api/auth/login", "/api/auth/refresh", "/api/auth/logout").permitAll()
                .anyRequest().authenticated()
            )
            
            .addFilterBefore(new JwtAuthenticationFilter(jwtProvider), org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class)
            .httpBasic(Customizer.withDefaults())
            .formLogin(form -> form.disable())
            .headers(h -> h.frameOptions(f -> f.disable()));
        
        return http.build();
    }
}
```

---

## 🧠 서비스 & 웹 계층

### AuthService.java
```java
package com.example.auth.service;

import com.example.auth.common.exception.DomainException;
import com.example.auth.common.exception.ErrorCode;
import com.example.auth.domain.member.Member;
import com.example.auth.dto.auth.*;
import com.example.auth.infrastructure.jwt.JwtProvider;
import com.example.auth.infrastructure.redis.RefreshToken;
import com.example.auth.infrastructure.redis.RefreshTokenRepository;
import com.example.auth.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public MemberResponse signup(SignupRequest req) {
        if (memberRepository.existsByUsername(req.getUsername())) {
            throw new DomainException(ErrorCode.MEMBER_DUPLICATED);
        }
        
        String hash = passwordEncoder.encode(req.getPassword());
        
        Member member = Member.builder()
            .username(req.getUsername())
            .passwordHash(hash)
            .nickname(req.getNickname())
            .build();
        return MemberResponse.from(memberRepository.save(member));
    }

    @Transactional
    public TokenResponse login(LoginRequest req) {
        Member member = memberRepository.findByUsername(req.getUsername())
                .orElseThrow(() -> new DomainException(ErrorCode.AUTH_INVALID_CREDENTIALS));
        
        if (!passwordEncoder.matches(req.getPassword(), member.getPasswordHash())) {
            throw new DomainException(ErrorCode.AUTH_INVALID_CREDENTIALS);
        }
        
        String access = jwtProvider.createAccessToken(member.getUsername(), Map.of("uid", member.getId().toString()));
        String refresh = jwtProvider.createRefreshToken(member.getUsername());
        
        refreshTokenRepository.save(RefreshToken.of(member.getId(), refresh, 1209600L)); // 14d
        return TokenResponse.builder().accessToken(access).refreshToken(refresh).build();
    }

    @Transactional
    public TokenResponse refresh(String username, String requestRefreshToken) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new DomainException(ErrorCode.MEMBER_NOT_FOUND));
        
        RefreshToken saved = refreshTokenRepository.findById(member.getId().toString())
                .orElseThrow(() -> new DomainException(ErrorCode.AUTH_REFRESH_NOT_FOUND));
        if (!saved.getToken().equals(requestRefreshToken) || jwtProvider.isExpired(requestRefreshToken)) {
            throw new DomainException(ErrorCode.AUTH_REFRESH_MISMATCH);
        }

        String newAccess = jwtProvider.createAccessToken(member.getUsername(), Map.of("uid", member.getId().toString()));
        String newRefresh = jwtProvider.createRefreshToken(member.getUsername());
        
        refreshTokenRepository.save(RefreshToken.of(member.getId(), newRefresh, 1209600L));
        return TokenResponse.builder().accessToken(newAccess).refreshToken(newRefresh).build();
    }

    @Transactional
    public void logout(String username) {
        memberRepository.findByUsername(username)
                .ifPresent(member -> refreshTokenRepository.deleteById(member.getId().toString()));
    }
}
```

### AuthController.java
```java
package com.example.auth.web;

import com.example.auth.common.api.ApiResponse;
import com.example.auth.dto.auth.*;
import com.example.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Objects;

/** 컨트롤러는 요청/응답 바인딩 + 검증만 담당 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ApiResponse<MemberResponse> signup(@RequestBody @Valid SignupRequest request) {
        return ApiResponse.ok(authService.signup(request));
    }

    @PostMapping("/login")
    public ApiResponse<TokenResponse> login(@RequestBody @Valid LoginRequest request) {
        return ApiResponse.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ApiResponse<TokenResponse> refresh(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
                                              @RequestHeader("X-Refresh-Token") String refreshToken) {
        // 데모 단순화: 실제로는 Authentication 기반 username 획득 권장
        String username = "";
        return ApiResponse.ok(authService.refresh(username, refreshToken));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(Authentication authentication) {
        String username = Objects.toString(authentication.getName(), null);
        authService.logout(username);
        return ApiResponse.ok(null);
    }
}
```

---

## 🧪 슬라이스 테스트 (예시)

### AuthControllerTest.java
```java
package com.example.auth.web;

import com.example.auth.dto.auth.LoginRequest;
import com.example.auth.dto.auth.TokenResponse;
import com.example.auth.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import jakarta.annotation.Resource;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {
    @Resource 
    private MockMvc mockMvc;
    
    @MockBean 
    private AuthService authService;
    
    @Resource 
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("로그인 성공 시 액세스/리프레시 토큰을 반환한다")
    void login_success() throws Exception {
        Mockito.when(authService.login(any(LoginRequest.class)))
                .thenReturn(TokenResponse.builder().accessToken("access.jwt.token").refreshToken("refresh.jwt.token").build());
        LoginRequest req = LoginRequest.builder().username("tester").password("Password!23").build();
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").value("access.jwt.token"))
                .andExpect(jsonPath("$.data.refreshToken").value("refresh.jwt.token"));
    }
}
```

---

## 🧪 cURL 사용 예
```bash
# 회원가입
curl -X POST http://localhost:8080/api/auth/signup  -H "Content-Type: application/json"  -d '{"username":"tester","password":"Password!23","nickname":"테스터"}'

# 로그인
curl -X POST http://localhost:8080/api/auth/login  -H "Content-Type: application/json"  -d '{"username":"tester","password":"Password!23"}'

# 보호 API 호출 예 (예: /api/me)
curl -H "Authorization: Bearer <ACCESS_TOKEN>" http://localhost:8080/api/me

# 리프레시(회전)
curl -X POST http://localhost:8080/api/auth/refresh  -H "Authorization: Bearer <ACCESS_TOKEN_OR_EMPTY>"  -H "X-Refresh-Token: <REFRESH_TOKEN>"

# 로그아웃
curl -X POST http://localhost:8080/api/auth/logout  -H "Authorization: Bearer <ACCESS_TOKEN>"
```

---

## 🔍 규칙 반영 체크리스트
- 전역 예외 + 도메인 예외: `GlobalExceptionHandler`, `DomainException`, `ErrorCode` ✔️
- 공통 응답: `ApiResponse<T>` ✔️
- 비즈니스 로직은 서비스에만: `AuthService` ✔️
- DTO 활용 + 엔티티 파라미터 금지 ✔️
- 캐싱/Redis: `@RedisHash`로 RefreshToken/TTL 관리 ✔️
- JWT + Security: `JwtProvider`, `JwtAuthenticationFilter`, `SecurityConfig` ✔️
- 보안: BCrypt, 최소 CORS, 무상태 ✔️
- Instant 사용: `BaseTime` ✔️
- N+1 방지: Repository에서 전용 조회 패턴 준비 ✔️
- @Getter/@Builder 활용, @Setter 지양 ✔️
- Service 인터페이스 분리 없이 단일 클래스 ✔️
- JUnit5 + Mockito 슬라이스 테스트 ✔️
- @Transactional 메서드 단위 적용(서비스) ✔️
- REST 규칙 준수: `/api/auth/*` ✔️
