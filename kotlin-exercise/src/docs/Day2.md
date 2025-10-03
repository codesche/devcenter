# Kotlin 중급 학습 정리 - Day 2


## 📚 오늘의 학습 목차

1. [컬렉션 기본](#1-컬렉션-기본)
2. [컬렉션 고급 처리](#2-컬렉션-고급-처리)
3. [람다 표현식](#3-람다-표현식)
4. [고차 함수](#4-고차-함수)
5. [Scope Functions](#5-scope-functions)
6. [실전 예제](#6-실전-예제)

---

## 1. 컬렉션 기본

### List, MutableList

```kotlin
// 읽기 전용
val videos = listOf("드라마", "예능", "영화")
println(videos[0])  // "드라마"

// 변경 가능
val playlist = mutableListOf("오징어 게임", "더 글로리")
playlist.add("이상한 변호사 우영우")
```

### Set, Map

```kotlin
// Set: 중복 제거
val genres = setOf("드라마", "예능", "드라마", "영화")
println(genres)  // [드라마, 예능, 영화]

// Map: 키-값 쌍
val ratings = mapOf(
    "오징어 게임" to 9.5,
    "더 글로리" to 9.2
)
println(ratings["오징어 게임"])  // 9.5
```

**💡 핵심 포인트**
- `listOf`, `setOf`, `mapOf`: 읽기 전용 (불변)
- `mutableListOf`, `mutableSetOf`, `mutableMapOf`: 변경 가능
- 서버 개발에서는 불변 컬렉션 선호

---

## 2. 컬렉션 고급 처리

### filter, map, reduce

```kotlin
data class Video(val id: Int, val title: String, val views: Int, val isPremium: Boolean)

val videos = listOf(
    Video(1, "오징어 게임", 1000000, true),
    Video(2, "무료 예능", 500000, false)
)

// filter: 조건 필터링
val premiumVideos = videos.filter { it.isPremium }

// map: 변환
val titles = videos.map { it.title }
val viewsInK = videos.map { it.views / 1000 }

// 체이닝
val premiumTitles = videos
    .filter { it.isPremium }
    .map { it.title }

// reduce: 축약
val totalViews = videos.map { it.views }.reduce { acc, views -> acc + views }

// 더 간단하게
val total = videos.sumOf { it.views }
```

### groupBy, sortedBy

```kotlin
// 그룹화
val groupedByType = videos.groupBy { it.isPremium }

// 정렬
val sorted = videos.sortedByDescending { it.views }
```

**💡 핵심 포인트**
- `filter`, `map`, `reduce`: 함수형 프로그래밍의 3대 연산
- 체이닝으로 복잡한 데이터 처리 가능
- API 응답 가공, DB 쿼리 결과 처리에 필수

---

## 3. 람다 표현식

### 기본 문법

```kotlin
// 전체 문법
val sum: (Int, Int) -> Int = { a, b -> a + b }

// 타입 추론
val multiply = { a: Int, b: Int -> a * b }

// 여러 줄
val complex = { a: Int, b: Int ->
    val result = a * b
    result  // 마지막 식이 반환값
}
```

### it 키워드

```kotlin
// 파라미터가 하나일 때
val numbers = listOf(1, 2, 3, 4, 5)
val doubled = numbers.map { it * 2 }

// 두 개 이상이면 명시적으로
val result = mapOf(1 to "one", 2 to "two")
    .map { (key, value) -> "$key: $value" }
```

**💡 핵심 포인트**
- 람다는 `{ }` 안에 작성
- 마지막 표현식이 반환값
- `it`: 단일 파라미터의 기본 이름

---

## 4. 고차 함수

### 함수를 파라미터로

```kotlin
fun processRequest(request: String, logger: (String) -> Unit) {
    logger("Processing: $request")
    // 처리
    logger("Completed: $request")
}

val simpleLogger: (String) -> Unit = { message -> 
    println("[LOG] $message") 
}

processRequest("GET /api/videos", simpleLogger)
```

### 함수를 반환

```kotlin
fun createMultiplier(factor: Int): (Int) -> Int {
    return { number -> number * factor }
}

val triple = createMultiplier(3)
println(triple(7))  // 21
```

### 실전 예제: 재시도 로직

```kotlin
fun <T> retry(times: Int, action: () -> T): T? {
    repeat(times) { attempt ->
        try {
            return action()
        } catch (e: Exception) {
            println("시도 ${attempt + 1} 실패")
            if (attempt == times - 1) throw e
        }
    }
    return null
}

// 사용
retry(3) {
    // API 호출 등
    apiCall()
}
```

**💡 핵심 포인트**
- 고차 함수: 함수를 파라미터로 받거나 반환
- 재시도, 로깅, 트랜잭션 등에 활용
- Spring Boot에서 AOP처럼 활용 가능

---

## 5. Scope Functions

### let: null 체크와 변환

```kotlin
val email: String? = "backend@tving.com"

// null 체크
email?.let { validEmail ->
    println("이메일: $validEmail")
}

// 변환
val length = email?.let { it.length } ?: 0
```

### run: 객체 초기화와 결과 반환

```kotlin
val user = User("홍길동", "hong@example.com")
val message = user.run {
    name = name.uppercase()
    email = email.lowercase()
    "설정 완료: $name"  // 반환
}
```

### apply: 객체 초기화

```kotlin
val newUser = User("", "").apply {
    name = "김철수"
    email = "kim@example.com"
}  // User 객체 자체 반환
```

### also: 부수 효과 (로깅)

```kotlin
val result = listOf(1, 2, 3, 4, 5)
    .filter { it > 2 }
    .also { println("필터링 결과: $it") }
    .map { it * 2 }
    .also { println("변환 결과: $it") }
```

### with: 객체를 리시버로

```kotlin
val numbers = mutableListOf(1, 2, 3)
with(numbers) {
    add(4)
    add(5)
    println("최종: $this")
}
```

### 비교 정리

| 함수 | 반환값 | this/it | 용도 |
|------|--------|---------|------|
| let | 람다 결과 | it | null 체크, 변환 |
| run | 람다 결과 | this | 초기화 + 결과 |
| apply | 객체 자체 | this | 객체 초기화 |
| also | 객체 자체 | it | 부수 효과 |
| with | 람다 결과 | this | 여러 함수 호출 |

**💡 핵심 포인트**
- Scope Functions: 코드를 간결하게
- Builder 패턴, 객체 초기화에 유용
- 로깅, 디버깅에 `also` 자주 사용

---

## 6. 실전 예제

### API 응답 처리

```kotlin
data class VideoResponse(
    val id: Long,
    val title: String,
    val duration: Int,
    val tags: List<String>
)

val responses = listOf(
    VideoResponse(1, "오징어 게임", 60, listOf("드라마", "스릴러")),
    VideoResponse(2, "피지컬100", 70, listOf("예능", "서바이벌"))
)

// 드라마 필터링
val dramas = responses
    .filter { "드라마" in it.tags }
    .also { println("드라마 개수: ${it.size}") }
    .map { it.title }

// 평균 러닝타임
val avgDuration = responses
    .map { it.duration }
    .average()

// 장르별 그룹화
val byGenre = responses
    .flatMap { video -> video.tags.map { tag -> tag to video.title } }
    .groupBy({ it.first }, { it.second })
```

### API 응답 래핑

```kotlin
data class ApiResult<T>(
    val success: Boolean,
    val data: T?,
    val error: String?
)

fun <T> wrapResponse(data: T?, errorMessage: String? = null): ApiResult<T> {
    return ApiResult(
        success = data != null,
        data = data,
        error = errorMessage
    )
}

val successResult = wrapResponse(response)
val errorResult = wrapResponse<VideoResponse>(null, "영상을 찾을 수 없습니다")
```

### 확장 함수 실전 예제

```kotlin
// List 확장
fun <T> List<T>.secondOrNull(): T? = if (this.size >= 2) this[1] else null

// String 확장
fun String.truncate(maxLength: Int): String {
    return if (this.length > maxLength) {
        "${this.take(maxLength)}..."
    } else {
        this
    }
}

// 사용
val list = listOf(1, 2, 3)
println(list.secondOrNull())  // 2

val longTitle = "아주 긴 제목의 영상입니다"
println(longTitle.truncate(10))  // "아주 긴 제목의 영..."
```

**💡 핵심 포인트**
- 실무에서는 filter, map, groupBy 조합을 자주 사용
- Generic을 활용한 공통 응답 래핑
- 함수형 스타일로 가독성 향상

---

## 📝 오늘의 학습 체크리스트

- [ ] List, Set, Map의 차이점 이해 완료
- [ ] filter, map, reduce 사용법 숙지
- [ ] 람다 표현식 문법 파악
- [ ] 고차 함수의 개념과 활용 이해
- [ ] Scope Functions 5가지 구분 가능
- [ ] 실전 예제 코드 필사 완료

---

## 🎯 TVING Backend Engineer 면접 대비

### 면접관: "컬렉션 처리에서 map과 flatMap의 차이는?"

**답변 예시:**
> "map은 각 요소를 1:1로 변환하지만, flatMap은 각 요소를 컬렉션으로 변환한 후 평탄화(flatten)합니다. 예를 들어, 영상의 태그 리스트를 모두 추출할 때 flatMap을 사용하면 중첩된 리스트가 하나의 리스트로 펼쳐집니다."

### 면접관: "let과 run의 차이는?"

**답변 예시:**
> "둘 다 람다 결과를 반환하지만, let은 it으로, run은 this로 객체에 접근합니다. let은 null 체크와 변환에, run은 객체 초기화와 계산 결과 반환에 주로 사용합니다."

### 면접관: "실무에서 고차 함수를 어떻게 활용하나?"

**답변 예시:**
> "재시도 로직, 트랜잭션 처리, 로깅 등 공통 관심사를 분리할 때 활용합니다. 예를 들어 retry 함수를 만들어 API 호출 실패 시 자동으로 재시도하도록 구현할 수 있습니다."

---

## 📌 다음 학습 계획 (Day 3)

### 실전 단계 예고
- **Coroutines 기초**: async, await, suspend
- **Spring Boot 연동**: Controller, Service 패턴
- **예외 처리**: try-catch, Result 타입
- **Null 안전성**: Elvis 연산자, requireNotNull

---

## 💪 학습 TIP

1. **필사 후 변형**: 예제를 자신의 상황에 맞게 변형
2. **체이닝 연습**: filter → map → groupBy 조합 연습
3. **성능 고려**: Sequence로 지연 연산 학습 (다음 단계)
4. **디버깅**: also를 활용한 중간 결과 확인

---

## 🔗 참고 자료

- [Kotlin Collections](https://kotlinlang.org/docs/collections-overview.html)
- [Scope Functions](https://kotlinlang.org/docs/scope-functions.html)
- [Higher-Order Functions](https://kotlinlang.org/docs/lambdas.html)
