# Kotlin SSR 게시판 (Spring Boot + Thymeleaf)

이 프로젝트는 **Spring Boot 3 + Kotlin + Thymeleaf + JPA(H2)** 기반의 서버사이드 렌더링(SSR) 게시판 최소 예제입니다.

## 주요 특징
- 전역 예외 처리(@ControllerAdvice) + 의미 있는 도메인 예외(`EntityNotFoundException`, `BusinessException`)
- 공통 API 응답 객체 `ApiResponse<T>` 제공
- 비즈니스 로직은 Service 계층에만 작성
- DTO 분리(요청/응답/도메인 DTO) 및 요청 DTO에 `@Valid` 검증
- `record` 미사용, `@Setter` 지양(불변성 유지)
- N+1 문제 방지: `@EntityGraph`, fetch join, `default_batch_fetch_size` 활용
- 부모-자식(Post-Comment) 삭제 전이 + orphanRemoval 반영
- RESTful 규칙형 URL (/posts, /posts/{id}, /posts/{id}/comments)
- JPA dirty-checking 반영
- SSR 기반의 Thymeleaf 템플릿 구성

---

## 프로젝트 구조
```
kboard
 ├─ build.gradle.kts
 ├─ settings.gradle.kts
 ├─ src
 │  ├─ main
 │  │  ├─ kotlin/com/example/kboard
 │  │  │  ├─ KboardApplication.kt
 │  │  │  ├─ global (에러 처리, 공통 응답, JPA 설정)
 │  │  │  ├─ domain (Member, Post, Comment 엔티티)
 │  │  │  ├─ service (PostService, CommentService)
 │  │  │  ├─ web/controller (PostController)
 │  │  │  ├─ web/dto (Request/Response DTO)
 │  │  │  └─ util (PageResponse)
 │  │  ├─ resources
 │  │  │  ├─ application.yml
 │  │  │  └─ templates (Thymeleaf 템플릿)
 │  └─ test
```

---

## Gradle 설정 (build.gradle.kts)
```kotlin
plugins {
    id("org.springframework.boot") version "3.3.4"
    id("io.spring.dependency-management") version "1.1.6"
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    kotlin("plugin.jpa") version "1.9.25"
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    runtimeOnly("com.h2database:h2")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
```

---

## application.yml
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:kboard;MODE=MySQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
    driver-class-name: org.h2.Driver
    username: sa
    password: 
  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        format_sql: true
        default_batch_fetch_size: 100
    open-in-view: false
  thymeleaf:
    cache: false

logging:
  level:
    org.hibernate.SQL: debug
    org.hibernate.orm.jdbc.bind: trace

server:
  port: 8080
```

---

## 엔티티 구조
- **Member**: 닉네임 기반 사용자
- **Post**: 제목, 내용, 작성자 / `Comment`와 1:N 관계
- **Comment**: 내용, 작성자, `Post`와 연관

`Post` 삭제 시 연관된 `Comment`도 자동 삭제 (Cascade + orphanRemoval).

---

## DTO 예시
**PostCreateRequest.kt**
```kotlin
class PostCreateRequest(
    @field:NotBlank 
    @field:Size(max = 200)
    val title: String,

    @field:NotBlank
    val content: String,

    @field:NotBlank
    val nickname: String
)
```

**PostResponse.kt**
```kotlin
class PostResponse(
    val id: Long,
    val title: String,
    val content: String,
    val authorNickname: String,
    val commentCount: Int
)
```

---

## 서비스 계층
**PostService.kt**
```kotlin
@Service
@Transactional(readOnly = true)
class PostService(private val postRepository: PostRepository) {
    fun list(page: Int, size: Int): PageResponse<PostResponse> { ... }
    fun getDetail(id: Long): Post { ... }

    @Transactional
    fun create(req: PostCreateRequest): Long { ... }

    @Transactional
    fun update(id: Long, req: PostUpdateRequest) { ... }

    @Transactional
    fun delete(id: Long) { ... }
}
```

---

## 컨트롤러
**PostController.kt**
```kotlin
@Controller
@RequestMapping
class PostController(
    private val postService: PostService,
    private val commentService: CommentService
) {
    @GetMapping("/posts")
    fun list(...) = "posts/list"

    @GetMapping("/posts/{id}")
    fun detail(...) = "posts/detail"

    @PostMapping("/posts")
    fun create(@Valid @ModelAttribute req: PostCreateRequest): String { ... }
}
```

---

## Thymeleaf 템플릿
- `posts/list.html` : 게시글 목록
- `posts/detail.html` : 게시글 상세 + 댓글
- `posts/form.html` : 글 작성/수정
- `fragments/layout.html` : 공통 레이아웃

---

## 실행 방법
```bash
./gradlew bootRun
# 접속
http://localhost:8080/posts
```

---

## 주의 사항
- 이 프로젝트는 학습용 예제이며, 인증/보안/검증 로직은 최소화했습니다.
- 실제 서비스 적용 시 Spring Security, CSRF, XSS 방어, 사용자 인증/권한 처리가 필요합니다.
