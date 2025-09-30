# Kotlin 기초 학습 정리 - [Day 1]

> **목표**: TVING Backend Engineer 지원을 위한 Kotlin 기초 다지기  
> **날짜**: 2025년 10월 1일  
> **학습 시간**: 약 1-2시간

---

## 📚 오늘의 학습 목차

1. [변수 선언 (val vs var)](#1-변수-선언)
2. [함수 기본](#2-함수-기본)
3. [조건문 (if, when)](#3-조건문)
4. [반복문 (for, while)](#4-반복문)
5. [클래스 기본](#5-클래스-기본)
6. [보너스: 실전 개념](#6-보너스-실전-개념)

---

## 1. 변수 선언

### val vs var

```kotlin
// val: 읽기 전용 (불변) - 권장!
val serviceName: String = "TVING"
val maxConnections = 1000  // 타입 추론 가능

// var: 변경 가능한 변수
var currentConnections = 500
currentConnections = 600  // 재할당 가능
```

### Nullable 타입

```kotlin
// null을 허용하려면 ? 사용
var optionalResponse: String? = null
optionalResponse = "Success"

// Safe Call 연산자
val length = optionalResponse?.length  // null이면 null 반환
```

**💡 핵심 포인트**
- 서버 개발에서는 `val` 사용 권장 (불변성, 동시성 안전)
- Null Safety는 Kotlin의 가장 큰 장점!

---

## 2. 함수 기본

### 기본 함수 선언

```kotlin
fun processRequest(userId: String): String {
    return "Processing request for user: $userId"
}
```

### 단일 표현식 함수

```kotlin
// = 기호로 간결하게 표현
fun calculateTotal(price: Int, quantity: Int) = price * quantity
```

### 기본 매개변수

```kotlin
fun createResponse(message: String, statusCode: Int = 200) {
    println("Response: $message (Status: $statusCode)")
}

// 호출
createResponse("Success")           // statusCode는 기본값 200
createResponse("Created", 201)      // statusCode를 201로 지정
```

**💡 핵심 포인트**
- 함수도 표현식처럼 값을 반환할 수 있음
- 기본 매개변수로 오버로딩 줄일 수 있음

---

## 3. 조건문

### if 표현식

```kotlin
val statusCode = 200

// if는 값을 반환하는 표현식
val status = if (statusCode == 200) {
    "OK"
} else if (statusCode == 404) {
    "Not Found"
} else {
    "Error"
}
```

### when 표현식

```kotlin
val httpMethod = "GET"
val action = when (httpMethod) {
    "GET" -> "Retrieve data"
    "POST" -> "Create resource"
    "PUT", "PATCH" -> "Update resource"
    "DELETE" -> "Remove resource"
    else -> "Unknown method"
}
```

**💡 핵심 포인트**
- `if`와 `when` 모두 표현식 (값 반환)
- `when`은 Java의 switch보다 훨씬 강력함

---

## 4. 반복문

### for 루프

```kotlin
// 범위 반복
for (i in 1..5) {
    println("Request $i")
}

// 리스트 순회
val endpoints = listOf("/api/videos", "/api/users", "/api/auth")
for (endpoint in endpoints) {
    println("Endpoint: $endpoint")
}

// 인덱스와 함께
for ((index, endpoint) in endpoints.withIndex()) {
    println("${index + 1}. $endpoint")
}
```

### while 루프

```kotlin
var retryCount = 0
while (retryCount < 3) {
    println("Retry attempt: $retryCount")
    retryCount++
}
```

**💡 핵심 포인트**
- `in` 키워드로 범위/컬렉션 순회
- `withIndex()`로 인덱스 접근

---

## 5. 클래스 기본

### 기본 클래스

```kotlin
class VideoContent(val id: Long, val title: String, val duration: Int) {
    fun getInfo(): String {
        return "Video: $title (${duration}min)"
    }
}

// 사용
val video = VideoContent(1, "응답하라 1988", 90)
println(video.getInfo())
```

### 데이터 클래스

```kotlin
data class ApiResponse(
    val status: Int,
    val message: String,
    val data: Any? = null
)

// 자동으로 제공되는 기능:
// - equals(), hashCode(), toString()
// - copy() 메서드

val response1 = ApiResponse(200, "Success", mapOf("id" to 1))
val response2 = response1.copy(status = 201)  // 일부만 변경
```

**💡 핵심 포인트**
- 생성자를 클래스 선언부에 작성 가능
- `data class`는 DTO, API Response에 완벽함
- `copy()`로 불변 객체 패턴 구현

---

## 6. 보너스: 실전 개념

### Sealed Class (봉인 클래스)

```kotlin
sealed class ApiResult {
    data class Success(val data: String) : ApiResult()
    data class Error(val message: String, val code: Int) : ApiResult()
    object Loading : ApiResult()
}

fun handleApiResult(result: ApiResult) {
    when (result) {
        is ApiResult.Loading -> println("Loading...")
        is ApiResult.Success -> println("Data: ${result.data}")
        is ApiResult.Error -> println("Error ${result.code}: ${result.message}")
    }
}
```

### Extension Function (확장 함수)

```kotlin
// 기존 클래스에 함수 추가
fun String.isValidEmail(): Boolean {
    return this.contains("@") && this.contains(".")
}

// 사용
val email = "backend@tving.com"
println(email.isValidEmail())  // true
```

**💡 핵심 포인트**
- Sealed Class는 API 응답 처리, 상태 관리에 필수
- Extension Function으로 기존 클래스 확장 가능

---

## 📝 오늘의 학습 체크리스트

- [ ] 변수 선언 (val vs var) 이해 완료
- [ ] Nullable 타입과 Safe Call 사용법 숙지
- [ ] when 표현식의 강력함 확인
- [ ] data class의 장점 파악
- [ ] Sealed Class 사용처 이해

---

## 🎯 TVING Backend Engineer 면접 대비 핵심 키워드

### 면접관: "Kotlin의 장점이 뭔가요?"

**답변 예시:**
1. **Null Safety**: `?` 연산자로 NPE 방지
2. **간결성**: Java보다 코드량 20-40% 감소
3. **표현식 기반**: if, when 등이 값을 반환
4. **확장 함수**: 기존 클래스 수정 없이 기능 추가
5. **data class**: DTO, API Response 작성이 매우 간편
6. **Coroutines**: 비동기 처리가 간결하고 효율적 (다음 학습 예정)

### 면접관: "어떻게 Kotlin을 공부했나요?"

**답변 예시:**
> "기초 문법부터 체계적으로 필사하며 학습했습니다. 
> 특히 TVING의 Backend Engineer 포지션에 지원하면서, 
> 서버 개발에서 자주 사용되는 Sealed Class(API 응답 처리), 
> Extension Function, data class 등을 중점적으로 연습했습니다. 
> 향후 Spring Boot와 Coroutines를 활용한 실전 프로젝트를 진행할 계획입니다."

---

## 📌 다음 학습 계획 (Day 2)

### 중급 단계 예고
- **컬렉션 처리**: map, filter, reduce
- **고차 함수**: 함수를 매개변수로
- **람다 표현식**: { } 문법
- **Scope Functions**: let, run, apply, also, with

### 실전 단계 예고
- **Coroutines**: 비동기 처리의 핵심
- **Spring Boot + Kotlin**: REST API 구현
- **Repository Pattern**: 데이터 접근 계층

---

## 💪 학습 TIP

1. **하루 1시간 필사**: 손으로 쓰면서 익히기
2. **실행하며 확인**: IntelliJ IDEA에서 직접 돌려보기
3. **작은 프로젝트**: 간단한 REST API 만들어보기
4. **공식 문서 읽기**: [kotlinlang.org](https://kotlinlang.org)

---

## 🔗 참고 자료

- [Kotlin 공식 문서](https://kotlinlang.org/docs/home.html)
- [Spring Boot + Kotlin 가이드](https://spring.io/guides/tutorials/spring-boot-kotlin/)
- [Kotlin Coroutines 가이드](https://kotlinlang.org/docs/coroutines-guide.html)
- [TVING 기술 블로그](https://team.tving.com)

---

**목표**: TVING Backend Engineer 합격! 🎯  
**다음 학습일**: 내일 또는 다음 세션