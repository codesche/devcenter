# 📑 Project 4 — Step 3: 외부 뉴스 API + 캐싱 서비스 + Swagger 정리

## ✅ 목표

* NewsAPI.org 연동(WebClient)
* DTO 매핑 → 컨트롤러 응답 모델 변환
* `@Cacheable` + Redis TTL 캐시 적용
* Micrometer 타이머로 레이턴시 수집
* **Swagger 관리 객체(ApiDoc) + 전역 OpenAPI 설정**으로 컨트롤러 깔끔하게 유지

---

## 📂 구현 경로

```
src/main/java/com/example/newscache/common/swagger
├─ OpenApiConfig.java
└─ ApiDoc.java

src/main/java/com/example/newscache/news
├─ controller
│   └─ NewsController.java
├─ dto
│   ├─ NewsSearchRequest.java
│   ├─ NewsArticleResponse.java
│   └─ NewsSearchResponse.java
├─ service
│   └─ NewsService.java
└─ infra
    ├─ ExternalNewsClient.java
    ├─ NewsApiClient.java
    └─ model
        └─ NewsApiPayload.java
```

---

## 🧱 핵심 구성

### OpenAPI(Swagger)

* `OpenApiConfig`: JWT Bearer 보안 스키마 전역 등록, API 메타정보 정의
* `ApiDoc`: 태그/요약/설명 문구를 **상수**로 관리 → 컨트롤러에서는 `@Tag(name = ApiDoc.News.TAG)` + `@Operation(summary = ..., description = ...)`만 기술

> 컨트롤러에 Swagger 어노테이션을 난발하지 않고, 문구 변화가 생겨도 \*\*한 곳(ApiDoc)\*\*만 고치면 됩니다.

### NewsAPI 클라이언트

* `NewsApiClient`: `/everything?q={query}&page={page}&pageSize={size}`
* `NewsApiPayload`: 외부 응답 매핑 모델 (publishedAt은 문자열)
* `NewsSearchResponse.from(payload)`: 변환 시 `Instant.parse()`로 시간 변환

### 캐싱 & 메트릭

* `@Cacheable(cacheNames = "news:search", key = "#query + ':' + #page + ':' + #size")`
* `news.search.ms` 타이머에 레이턴시 기록(간단 비교용)

---

## 🔧 Gradle (추가)

`springdoc-openapi` 의존성 추가:

```groovy
implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0'
```

> UI 경로: `/swagger-ui/index.html`

---

## 🌐 application.yml (리마인드)

```yaml
external:
  news:
    base-url: https://newsapi.org/v2
    api-key: ${NEWS_API_KEY}
```

* API Key는 **환경변수/시크릿 매니저**로 주입(앞서 메모 저장)

---

## 🧪 빠른 점검 체크리스트

* [ ] `/swagger-ui/index.html` 로딩 확인
* [ ] `GET /api/news?query=AI&page=1&size=10` 정상 동작
* [ ] 동일 요청 2회 호출 시 2번째 응답 레이턴시 단축(캐시 작동)
* [ ] Actuator `metrics`에서 `news.search.ms` 확인

---

## 📌 다음 단계

👉 Step 4: **벤치 엔드포인트/메트릭 시각화(Actuator + Prometheus) + 캐시 TTL 실험(5m/1m 스큐) + 슬라이스/단위테스트 보강**
