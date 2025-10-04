# Kotlin 실전 학습 정리 - Day 3

## 📚 오늘의 학습 목차

1. [예외 처리](#1-예외-처리)
2. [Null 안전성](#2-null-안전성)
3. [Result 타입](#3-result-타입)
4. [실전: REST API 시뮬레이션](#4-실전-rest-api-시뮬레이션)

---

## 1. 예외 처리

### 기본 try-catch

```kotlin
fun divide(a: Int, b: Int): Int {
    return try {
        a / b
    } catch (e: ArithmeticException) {
        println("에러: ${e.message}")
        0
    } finally {
        println("finally 블록 실행")
    }
}

println("10 / 2 = ${divide(10, 2)}")  // 5
println("10 / 0 = ${divide(10, 0)}")  // 0
```

### 커스텀 예외

```kotlin
class VideoNotFoundException(message: String) : Exception(message)

fun findVideo(id: Long): String {
    if (id <= 0) {
        throw VideoNotFoundException("영상 ID가 유효하지 않습니다: $id")
    }
    return "Video-$id"
}

try {
    findVideo(1)   // 성공
    findVideo(-1)  // 예외 발생
} catch (e: VideoNotFoundException) {
    println("커스텀 에러: ${e.message}")
}
```

### runCatching: 예외를 Result로 변환

```kotlin
val result1 = runCatching { divide(10, 2) }
val result2 = runCatching { divide(10, 0) }

println("성공: ${result1.getOrNull()}")  // 5
println("실패: ${result2.exceptionOrNull()?.message}")  // / by zero
```

**💡 핵심 포인트**
- Kotlin은 Checked Exception이 없음 (모두 Unchecked)
- `runCatching`: 예외를 안전하게 Result로 감싸기
- 함수형 스타일의 예외 처리 가능
- try-catch보다 더 간결하고 체이닝 가능

---

## 2. Null 안전성

### Safe Call 연산자 ?.

```kotlin
val name: String? = "TVING"
println(name?.length)  // 5

val nullName: String? = null
println(nullName?.length)  // null (NPE 발생 안 함)
```

### Elvis 연산자 ?:

```kotlin
val length = nullName?.length ?: 0
println(length)  // 0 (기본값)

// 함수와 조합
fun getDisplayName(name: String?): String {
    return name?.uppercase() ?: "이름 없음"
}
```

### let과 조합

```kotlin
val name: String? = "TVING"

name?.let {
    println("이름이 있음: $it")
}

nullName?.let {
    println("이 줄은 실행되지 않음")
} ?: println("null이므로 이 줄 실행")
```

### 안전한 캐스팅 as?

```kotlin
val obj: Any = "문자열"

val str: String? = obj as? String  // 성공: "문자열"
val num: Int? = obj as? Int        // 실패: null (예외 발생 안 함)

println("문자열 캐스팅: $str")
println("숫자 캐스팅: $num")
```

### requireNotNull, checkNotNull

```kotlin
fun processUser(name: String?) {
    val validName = requireNotNull(name) { "이름은 필수입니다" }
    println("처리: $validName")
}

try {
    processUser("홍길동")  // 성공
    processUser(null)      // IllegalArgumentException 발생
} catch (e: IllegalArgumentException) {
    println("에러: ${e.message}")
}
```

### !! 연산자 (권장하지 않음)

```kotlin
// 사용하지 마세요!!!!
// val length = nullName!!.length  // NullPointerException 발생
```

**💡 핵심 포인트**
- `?.`: null일 때 null 반환 (안전한 호출)
- `?:`: null일 때 기본값 제공
- `as?`: 안전한 타입 캐스팅
- `requireNotNull`: 명시적인 null 검증
- `!!`: 절대 사용하지 말 것 (NPE 발생 가능)

---

## 3. Result 타입

### Result를 반환하는 함수

```kotlin
fun divideWithResult(a: Int, b: Int): Result<Int> {
    return if (b == 0) {
        Result.failure(ArithmeticException("0으로 나눌 수 없습니다"))
    } else {
        Result.success(a / b)
    }
}
```

### Result 처리 방법 1: getOrNull

```kotlin
val result1 = divideWithResult(10, 2)
println(result1.getOrNull())  // 5

val result2 = divideWithResult(10, 0)
println(result2.getOrNull())  // null
```

### Result 처리 방법 2: getOrDefault

```kotlin
val result = divideWithResult(10, 0).getOrDefault(0)
println(result)  // 0
```

### Result 처리 방법 3: fold

```kotlin
divideWithResult(10, 2).fold(
    onSuccess = { println("성공: $it") },
    onFailure = { println("실패: ${it.message}") }
)

divideWithResult(10, 0).fold(
    onSuccess = { println("성공: $it") },
    onFailure = { println("실패: ${it.message}") }
)
```

### Result 체이닝

```kotlin
val finalResult = divideWithResult(100, 2)
    .map { it * 2 }   // 성공시 변환: 50 -> 100
    .map { it + 10 }  // 100 -> 110
    .getOrDefault(0)

println(finalResult)  // 110
```

### onSuccess, onFailure 메서드

```kotlin
divideWithResult(10, 2)
    .onSuccess { println("계산 성공: $it") }
    .onFailure { println("계산 실패: ${it.message}") }
```

**💡 핵심 포인트**
- `Result<T>`: 성공(Success) 또는 실패(Failure)를 표현
- 예외를 값으로 다룰 수 있음
- `fold`: 성공/실패 모두 처리
- `map`: 성공일 때만 변환
- 함수형 에러 처리의 핵심 패턴

---

## 4. 실전: REST API 시뮬레이션

### DTO 정의

```kotlin
data class VideoRequest(val title: String, val duration: Int)

data class VideoResponse(
    val id: Long,
    val title: String,
    val duration: Int,
    val createdAt: String
)

data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val error: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
```

### Service Layer

```kotlin
class VideoService {
    private val videos = mutableMapOf<Long, VideoResponse>()
    private var nextId = 1L
    
    fun createVideo(request: VideoRequest): Result<VideoResponse> {
        return runCatching {
            // DB 저장 시뮬레이션
            Thread.sleep(100)
            
            if (request.title.isBlank()) {
                throw IllegalArgumentException("제목은 필수입니다")
            }
            
            val video = VideoResponse(
                id = nextId++,
                title = request.title,
                duration = request.duration,
                createdAt = "2025-10-03"
            )
            
            videos[video.id] = video
            video
        }
    }
    
    fun getVideo(id: Long): Result<VideoResponse> {
        return runCatching {
            // DB 조회 시뮬레이션
            Thread.sleep(50)
            videos[id] ?: throw NoSuchElementException("영상을 찾을 수 없습니다: $id")
        }
    }
    
    fun getAllVideos(): Result<List<VideoResponse>> {
        return runCatching {
            Thread.sleep(50)
            videos.values.toList()
        }
    }
    
    fun updateVideo(id: Long, request: VideoRequest): Result<VideoResponse> {
        return runCatching {
            Thread.sleep(50)
            val existing = videos[id] 
                ?: throw NoSuchElementException("영상을 찾을 수 없습니다: $id")
            
            val updated = existing.copy(
                title = request.title,
                duration = request.duration
            )
            videos[id] = updated
            updated
        }
    }
    
    fun deleteVideo(id: Long): Result<Unit> {
        return runCatching {
            Thread.sleep(50)
            videos.remove(id) ?: throw NoSuchElementException("영상을 찾을 수 없습니다: $id")
        }
    }
}
```

### Controller Layer

```kotlin
class VideoController(private val service: VideoService) {
    fun createVideo(request: VideoRequest): ApiResponse<VideoResponse> {
        return service.createVideo(request).fold(
            onSuccess = { video ->
                ApiResponse(success = true, data = video)
            },
            onFailure = { error ->
                ApiResponse(success = false, error = error.message)
            }
        )
    }
    
    fun getVideo(id: Long): ApiResponse<VideoResponse> {
        return service.getVideo(id).fold(
            onSuccess = { video ->
                ApiResponse(success = true, data = video)
            },
            onFailure = { error ->
                ApiResponse(success = false, error = error.message)
            }
        )
    }
    
    fun getAllVideos(): ApiResponse<List<VideoResponse>> {
        return service.getAllVideos().fold(
            onSuccess = { videos ->
                ApiResponse(success = true, data = videos)
            },
            onFailure = { error ->
                ApiResponse(success = false, error = error.message)
            }
        )
    }
}
```

### 실행 예제

```kotlin
val service = VideoService()
val controller = VideoController(service)

// POST /api/videos
val createResponse = controller.createVideo(
    VideoRequest("오징어 게임", 60)
)
println(createResponse.data?.title)  // "오징어 게임"

// GET /api/videos/1
val getResponse = controller.getVideo(1)
println(getResponse.data)  // VideoResponse(...)

// GET /api/videos
val getAllResponse = controller.getAllVideos()
println(getAllResponse.data?.size)  // 영상 개수

// GET /api/videos/999 (없는 ID)
val notFoundResponse = controller.getVideo(999)
println(notFoundResponse.error)  // "영상을 찾을 수 없습니다: 999"

// POST /api/videos (잘못된 요청)
val invalidResponse = controller.createVideo(
    VideoRequest("", 60)
)
println(invalidResponse.error)  // "제목은 필수입니다"
```

**💡 핵심 포인트**
- **3계층 구조**: DTO - Service - Controller
- **Service**: 비즈니스 로직, Result 반환
- **Controller**: HTTP 처리, ApiResponse 반환
- **fold**: Result를 ApiResponse로 변환
- **runCatching**: 예외를 Result로 자동 변환
- 실제 Spring Boot 패턴과 동일한 구조

---

## 📝 오늘의 학습 체크리스트

- [ ] try-catch와 runCatching 차이점 파악
- [ ] Null 안전성 연산자 5가지 숙지 (?, ?:, as?, requireNotNull, !!)
- [ ] Result 타입 활용법 이해 (fold, map, getOrNull)
- [ ] Service-Controller 패턴 구현 완료
- [ ] CRUD 전체 흐름 파악 완료

---

## 🎯 TVING Backend Engineer 면접 대비

### 면접관: "Kotlin의 Null Safety가 왜 중요한가?"

**답변 예시:**
> "Kotlin의 Null Safety는 컴파일 타임에 NPE를 방지할 수 있어 런타임 안정성을 크게 향상시킵니다. ?와 ?: 연산자로 null을 명시적으로 처리하고, requireNotNull로 비즈니스 로직에서 필수값을 검증할 수 있습니다. 이는 특히 대규모 서비스에서 예상치 못한 NPE로 인한 장애를 사전에 방지할 수 있습니다."

### 면접관: "Result 타입을 왜 사용하나?"

**답변 예시:**
> "Result 타입은 성공과 실패를 명시적으로 표현하여 예외를 값으로 다룰 수 있게 합니다. try-catch보다 함수형 스타일로 에러를 처리할 수 있고, fold나 map 같은 메서드로 체이닝이 가능해 코드가 더 읽기 쉽고 안전합니다. 특히 Service 계층에서 Result를 반환하고 Controller에서 fold로 HTTP 응답을 만드는 패턴이 효과적입니다."

### 면접관: "Service와 Controller를 왜 분리하나?"

**답변 예시:**
> "관심사의 분리(Separation of Concerns)를 위해서입니다. Service는 순수한 비즈니스 로직에 집중하고, Controller는 HTTP 요청/응답 처리에 집중합니다. 이렇게 분리하면 테스트가 쉽고, 코드 재사용성이 높으며, 유지보수가 용이합니다. 또한 Service는 Result를 반환하고 Controller는 ApiResponse로 변환하는 명확한 책임 분리가 가능합니다."

---

## 📌 다음 학습 계획 (Day 4+)

### Coroutines (비동기 처리)
- **suspend 함수**: 중단 가능한 함수
- **launch vs async**: 차이점과 사용법
- **병렬 처리**: 성능 최적화

### Spring Boot 통합
- **프로젝트 설정**: build.gradle.kts
- **@RestController, @Service**: Spring 어노테이션
- **JPA 연동**: 데이터베이스 연결
- **실전 CRUD API**: 완성된 백엔드 서버

---

## 💪 학습 TIP

1. **실행하며 학습**: IntelliJ에서 직접 실행해보기
2. **Result 패턴 익히기**: try-catch 대신 Result 사용 습관화
3. **계층 구조 이해**: Service-Controller 분리의 이점 파악
4. **에러 처리**: fold를 활용한 우아한 에러 처리
5. **코드 변형**: 예제를 자신만의 도메인으로 변경해보기

---

## 🔗 참고 자료

- [Kotlin Result 타입](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-result/)
- [Null Safety](https://kotlinlang.org/docs/null-safety.html)
- [Exception Handling](https://kotlinlang.org/docs/exceptions.html)
- [Spring Boot + Kotlin](https://spring.io/guides/tutorials/spring-boot-kotlin/)

---

## 📊 학습 진도 요약

| Day | 주제 | 난이도 | 완료 |
|-----|------|--------|------|
| Day 1 | 기초 문법 | ⭐ | ✅ |
| Day 2 | 컬렉션 & 람다 | ⭐⭐ | ✅ |
| Day 3 | 예외 처리 & REST API | ⭐⭐⭐ | ✅ |
| Day 4+ | Coroutines & Spring Boot | ⭐⭐⭐⭐ | 예정 |

