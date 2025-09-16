# ✅ ToDo List API (Spring Boot)

간단하지만 실무형 구조로 만든 **REST 기반 ToDo 리스트 API**입니다.
- 3계층 구조(Controller → Service → Repository)
- DTO 중심 입출력, 엔티티 노출 금지
- 전역 예외 처리 및 공통 응답 포맷(`ApiResponse<T>`)
- JPA Auditing으로 생성/수정 시각 자동 관리
- UUID를 기본 식별자로 사용

> 본 README는 **API 사용 예시**, **실제 Request/Response 샘플**, **Postman 컬렉션**까지 포함합니다.

---

## 🧰 Tech Stack
- Java 17, Spring Boot 3.x
- Spring Web, Spring Data JPA, Jakarta Validation
- DB: PostgreSQL (테스트는 H2 PostgreSQL 모드)

---

## 📁 패키지 구조(요약)
```
com.example.todo
 ├─ domain
 │   └─ todo
 │       ├─ entity
 │       │   └─ Todo.java
 │       ├─ dto
 │       │   ├─ TodoCreateRequest.java
 │       │   ├─ TodoUpdateRequest.java
 │       │   └─ TodoResponse.java
 │       ├─ repository
 │       │   └─ TodoRepository.java
 │       └─ service
 │           ├─ TodoService.java
 │           └─ TodoServiceImpl.java
 └─ global
     ├─ api
     │   └─ ApiResponse.java
     ├─ exception
     │   ├─ GlobalExceptionHandler.java
     │   └─ TodoNotFoundException.java
     └─ jpa
         └─ JpaAuditingConfig.java
```

---

## 🚀 빠른 시작 (로컬 실행)
1) `application.yml` 예시
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/todo
    username: postgres
    password: postgres
  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        format_sql: true
  jackson:
    serialization:
      WRITE_DATES_AS_TIMESTAMPS: false
server:
  port: 8080
```

2) 빌드 & 실행
```bash
./gradlew clean build
./gradlew bootRun
```

3) 테스트 프로필(H2) — JPA 슬라이스 테스트용
```yaml
# src/test/resources/application-test.yml
spring:
  datasource:
    url: jdbc:h2:mem:todo;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
    driver-class-name: org.h2.Driver
    username: sa
    password:
  jpa:
    hibernate:
      ddl-auto: create-drop
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true
    show-sql: false
```

---

## 📚 공통 응답 포맷
모든 응답은 다음의 래퍼를 따릅니다.
```json
{
  "success": true,
  "data": {},
  "message": "optional",
  "timestamp": "2025-09-16T00:00:00Z"
}
```

에러 시:
```json
{
  "success": false,
  "data": null,
  "message": "에러 메시지",
  "timestamp": "2025-09-16T00:00:00Z"
}
```

---

## 🔗 API 엔드포인트 요약
| 메서드 | 경로 | 설명 |
|---|---|---|
| `POST` | `/api/todos` | ToDo 생성 |
| `GET` | `/api/todos/{id}` | 단건 조회 |
| `GET` | `/api/todos` | 페이지 조회 (page,size,sort) |
| `PATCH` | `/api/todos/{id}` | 부분 수정(제목/설명/완료여부) |
| `DELETE` | `/api/todos/{id}` | 삭제 |

### Pageable 파라미터 예시
- `GET /api/todos?page=0&size=10&sort=createdAt,desc`

---

## 🧪 실제 요청/응답 예시

### 1) 생성 — `POST /api/todos`
**Request**
```http
POST /api/todos HTTP/1.1
Content-Type: application/json

{
  "title": "Implement ToDo API",
  "description": "Service/Controller/Tests",
  "completed": false
}
```
**Response (200)**
```json
{
  "success": true,
  "data": {
    "id": "2d3f1d40-9f61-46c3-97e8-9e90d6b1a8f0",
    "title": "Implement ToDo API",
    "description": "Service/Controller/Tests",
    "completed": false,
    "createdAt": "2025-09-16T01:23:45",
    "updatedAt": "2025-09-16T01:23:45"
  },
  "message": "created",
  "timestamp": "2025-09-16T01:23:45Z"
}
```

> **검증 에러(400)**: `title`이 비었을 때
```json
{
  "success": false,
  "data": null,
  "message": "Validation failed: {title=제목은 필수입니다.}",
  "timestamp": "2025-09-16T01:23:45Z"
}
```

---

### 2) 단건 조회 — `GET /api/todos/{id}`
**Request**
```http
GET /api/todos/2d3f1d40-9f61-46c3-97e8-9e90d6b1a8f0 HTTP/1.1
```
**Response (200)**
```json
{
  "success": true,
  "data": {
    "id": "2d3f1d40-9f61-46c3-97e8-9e90d6b1a8f0",
    "title": "Implement ToDo API",
    "description": "Service/Controller/Tests",
    "completed": false,
    "createdAt": "2025-09-16T01:23:45",
    "updatedAt": "2025-09-16T01:23:45"
  },
  "timestamp": "2025-09-16T01:23:46Z"
}
```

> **존재하지 않음(404)**
```json
{
  "success": false,
  "data": null,
  "message": "Todo not found: 11111111-2222-3333-4444-555555555555",
  "timestamp": "2025-09-16T01:23:47Z"
}
```

---

### 3) 페이지 조회 — `GET /api/todos`
**Request**
```http
GET /api/todos?page=0&size=2&sort=createdAt,desc HTTP/1.1
```
**Response (200)**
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": "5a5b0c55-14a2-4217-8c47-7cc4d5c3f311",
        "title": "Write Controller tests",
        "description": "MockMvc with @WebMvcTest",
        "completed": true,
        "createdAt": "2025-09-16T01:30:00",
        "updatedAt": "2025-09-16T01:40:00"
      },
      {
        "id": "2d3f1d40-9f61-46c3-97e8-9e90d6b1a8f0",
        "title": "Implement ToDo API",
        "description": "Service/Controller/Tests",
        "completed": false,
        "createdAt": "2025-09-16T01:23:45",
        "updatedAt": "2025-09-16T01:23:45"
      }
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 2
    },
    "totalElements": 5,
    "totalPages": 3,
    "last": false,
    "first": true,
    "size": 2,
    "number": 0,
    "sort": { "sorted": true, "unsorted": false, "empty": false },
    "numberOfElements": 2,
    "empty": false
  },
  "timestamp": "2025-09-16T01:40:10Z"
}
```

---

### 4) 부분 수정 — `PATCH /api/todos/{id}`
**Request**
```http
PATCH /api/todos/2d3f1d40-9f61-46c3-97e8-9e90d6b1a8f0 HTTP/1.1
Content-Type: application/json

{
  "title": "Implement ToDo API (final)",
  "completed": true
}
```
**Response (200)**
```json
{
  "success": true,
  "data": {
    "id": "2d3f1d40-9f61-46c3-97e8-9e90d6b1a8f0",
    "title": "Implement ToDo API (final)",
    "description": "Service/Controller/Tests",
    "completed": true,
    "createdAt": "2025-09-16T01:23:45",
    "updatedAt": "2025-09-16T01:45:12"
  },
  "message": "updated",
  "timestamp": "2025-09-16T01:45:12Z"
}
```

---

### 5) 삭제 — `DELETE /api/todos/{id}`
**Request**
```http
DELETE /api/todos/2d3f1d40-9f61-46c3-97e8-9e90d6b1a8f0 HTTP/1.1
```
**Response (200)**
```json
{
  "success": true,
  "data": null,
  "message": "deleted",
  "timestamp": "2025-09-16T01:50:00Z"
}
```

---

## 🧨 cURL / HTTPie 예시

### cURL
```bash
# 생성
curl -X POST "http://localhost:8080/api/todos" \
  -H "Content-Type: application/json" \
  -d '{
    "title":"Implement ToDo API",
    "description":"Service/Controller/Tests",
    "completed":false
  }'

# 단건 조회
curl "http://localhost:8080/api/todos/{id}"

# 페이지 조회
curl "http://localhost:8080/api/todos?page=0&size=10&sort=createdAt,desc"

# 부분 수정
curl -X PATCH "http://localhost:8080/api/todos/{id}" \
  -H "Content-Type: application/json" \
  -d '{"title":"final","completed":true}'

# 삭제
curl -X DELETE "http://localhost:8080/api/todos/{id}"
```

### HTTPie
```bash
http POST :8080/api/todos title="Implement ToDo API" description="Service/Controller/Tests" completed:=false
http :8080/api/todos/{id}
http :8080/api/todos page==0 size==10 sort==createdAt,desc
http PATCH :8080/api/todos/{id} title="final" completed:=true
http DELETE :8080/api/todos/{id}
```

---

## 🧪 테스트 실행
```bash
./gradlew test
```
- Service 단위 테스트: Mockito 기반 CRUD/예외 흐름 검증
- Controller 슬라이스 테스트: `@WebMvcTest` + 전역 예외 핸들러 임포트
    - **참고**: `MockMvc`는 필드 주입으로 사용하거나, 생성자 주입 시 `@TestConstructor(autowireMode = ALL)` 필요
- Repository 슬라이스 테스트: `@DataJpaTest` + H2(PostgreSQL 모드) + Auditing 활성화

---

## 🧰 트러블슈팅
- 에러: `No ParameterResolver registered for parameter [MockMvc mockMvc] ...`
    - 해결: 테스트 클래스에서 `MockMvc` **필드 주입**(`@Autowired private MockMvc mockMvc;`)으로 변경
    - 또는 `@TestConstructor(autowireMode = ALL)` 사용

---

## 📦 Postman 컬렉션 (v2.1) — Import JSON
> 아래 JSON을 파일로 저장(예: `Todo.postman_collection.json`) 후 Postman에서 **Import**하세요. `{{baseUrl}}`는 환경 변수입니다.

```json
{
  "info": {
    "name": "ToDo List API (Spring Boot)",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json",
    "description": "CRUD for ToDo with ApiResponse wrapper"
  },
  "item": [
    {
      "name": "Create Todo",
      "request": {
        "method": "POST",
        "header": [
          {"key": "Content-Type", "value": "application/json"}
        ],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"title\": \"Implement ToDo API\",\n  \"description\": \"Service/Controller/Tests\",\n  \"completed\": false\n}"
        },
        "url": {"raw": "{{baseUrl}}/api/todos", "host": ["{{baseUrl}}"], "path": ["api","todos"]}
      }
    },
    {
      "name": "Get Todo",
      "request": {
        "method": "GET",
        "url": {"raw": "{{baseUrl}}/api/todos/{{todoId}}", "host": ["{{baseUrl}}"], "path": ["api","todos","{{todoId}}"]}
      }
    },
    {
      "name": "List Todos",
      "request": {
        "method": "GET",
        "url": {
          "raw": "{{baseUrl}}/api/todos?page=0&size=10&sort=createdAt,desc",
          "host": ["{{baseUrl}}"],
          "path": ["api","todos"],
          "query": [
            {"key": "page", "value": "0"},
            {"key": "size", "value": "10"},
            {"key": "sort", "value": "createdAt,desc"}
          ]
        }
      }
    },
    {
      "name": "Update Todo (PATCH)",
      "request": {
        "method": "PATCH",
        "header": [
          {"key": "Content-Type", "value": "application/json"}
        ],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"title\": \"final\",\n  \"completed\": true\n}"
        },
        "url": {"raw": "{{baseUrl}}/api/todos/{{todoId}}", "host": ["{{baseUrl}}"], "path": ["api","todos","{{todoId}}"]}
      }
    },
    {
      "name": "Delete Todo",
      "request": {
        "method": "DELETE",
        "url": {"raw": "{{baseUrl}}/api/todos/{{todoId}}", "host": ["{{baseUrl}}"], "path": ["api","todos","{{todoId}}"]}
      }
    }
  ],
  "variable": [
    {"key": "baseUrl", "value": "http://localhost:8080"},
    {"key": "todoId", "value": "2d3f1d40-9f61-46c3-97e8-9e90d6b1a8f0"}
  ]
}
```

### Postman 환경 (선택)
```json
{
  "id": "c7c1f3e5-1111-2222-3333-444444444444",
  "name": "Local",
  "values": [
    {"key": "baseUrl", "value": "http://localhost:8080", "enabled": true},
    {"key": "todoId", "value": "2d3f1d40-9f61-46c3-97e8-9e90d6b1a8f0", "enabled": true}
  ],
  "_postman_variable_scope": "environment",
  "_postman_exported_using": "Postman/10.x"
}
```

> 컬렉션 Import 후 `Create Todo`를 실행하고 생성 응답의 `id`를 `todoId`에 넣으면 나머지 요청들을 바로 시도할 수 있습니다.

---

## 📝 변경 이력 (요약)
- 1단계: Entity & DTO 정의 (UUID, Auditing)
- 2단계: Repository / Service / Controller + 전역 예외 & 공통 응답
- 3단계: 테스트(서비스/컨트롤러/리포지토리), H2(PostgreSQL 모드) 설정

필요하면 이 README를 `README.md`로 저장해 루트에 배치하시고, 팀 저장소에 올려 협업하세요. 다른 포맷(한글/영문 분리, API 스펙 표준화 등)도 요청하면 정리해 드릴게요.

