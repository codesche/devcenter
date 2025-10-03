package exercise

// Day2
fun main() {
    println("=== 1. 컬렉션 기본 ===")
    collectionBasics()

    println("\n=== 2. 컬렉션 고급 처리 ===")
    collectionAdvanced()

    println("\n=== 3. 람다 표현식 ===")
    lambdaExample()

    println("\n=== 4. 고차 함수 ===")
    higherOrderFunctions()

    println("\n=== 5. Scope Functions ===")
    scopeFunctionsExample()

    println("\n=== 6. 실전 예제 ===")
    practicalExample()

}

// 1. 컬렉션 기본
fun collectionBasics() {
    // List (읽기 전용)
    val videos = listOf("드라마", "예능", "영화")
    println("전체 영상: $videos")
    println("첫 번째: ${videos[0]}")
    println("크기: ${videos.size}")

    // MutableList (변경 가능)
    val playlist = mutableListOf("오징어 게임", "더 글로리")
    playlist.add("이상한 변호사 우영우")
    println("플레이리스트: $playlist")

    // Set (중복 제거)
    val genres = setOf("드라마", "예능", "드라마", "영화")
    println("장르: $genres")      // 중복 제거됨

    // Map (키-값 쌍)
    val userRatings = mapOf(
        "오징어 게임" to 9.5,
        "더 글로리" to 9.2,
        "이상한 변호사 우영우" to 9.0
    )

    println("평점: ${userRatings["오징어 게임"]}")

}

// 2. 컬렉션 고급 처리 (map, filter, reduce)
fun collectionAdvanced() {
    data class Video(val id: Int, val title: String, val views: Int, val isPremium: Boolean)

    val videos = listOf(
        Video(1, "오징어 게임", 1000000, true),
        Video(2, "더 글로리", 800000, true),
        Video(3, "무료 예능", 500000, false),
        Video(4, "무료 드라마", 300000, false)
    )

    // filter: 조건에 맞는 요소만 선택
    val premiumVideos = videos.filter { it.isPremium }
    println("프리미엄 영상 개수: ${premiumVideos.size}")

    // map: 각 요소를 반환
    val titles = videos.map { it. title }
    println("모든 제목: $titles")

    val viewsInk = videos.map { it.views / 1000 }
    println("조회수(K): $viewsInk")

    // filter + map 조합
    val premiumTitles = videos
        .filter { it.isPremium }
        .map { it.title }
        println("프리미엄 제목들: $premiumTitles")

    // reduce: 값들을 하나로 축약
    val totalViews = videos.map { it.views }.reduce { acc, views -> acc + views }
    println("전체 조회수: $totalViews")

    // sum으로 더 간단하게
    val totalViews2 = videos.sumOf { it.views }
    println("전체 조회수(sum): $totalViews2")

    // groupBy: 그룹화
    val groupedByType = videos.groupBy { it.isPremium }
    println("프리미엄 여부로 그룹화: ${groupedByType.keys}")

    // sortedBy: 정렬
    val sortedByViews = videos.sortedByDescending { it.views }
    println("조회수 내림차순: ${sortedByViews.map { it.title }}")

}

// 3. 람다 표현식
fun lambdaExample() {
    // 기본 람다
    val sum: (Int, Int) -> Int = { a, b -> a + b }
    println("10 + 20 = ${sum(10, 20)}")

    // 람다의 마지막 식이 반환값
    val multiply = { a: Int, b: Int ->
        val result = a + b
        result      // 반환
    }
    println("5 * 3 = ${multiply(5, 3)}")

    // it: 파라미터가 하나일 때 사용
    val numbers = listOf(1, 2, 3, 4, 5)
    val doubled = numbers.map { it * 2 }
    println("두 배: $doubled")

    // 언더스코어: 사용하지 않는 파라미터
    val pairs = listOf(1 to "one", 2 to "two", 3 to "three")
    val values = pairs.map { (_, value) -> value }
    println("값들만: $values")

}

// 4. 고차함수 (함수를 파라미터로 받거나 반환하는 함수)
fun higherOrderFunctions() {
    // 함수를 파라미터로 받기
    fun processRequest(request: String, logger: (String) -> Unit) {
        logger("Processing: $request")
        // 처리 로직
        logger("Completed: $request")
    }

    val simpleLogger: (String) -> Unit = { message -> println("[LOG] $message") }
    processRequest("GET /api/videos", simpleLogger)

    // 함수를 반환하기
    fun createMultiplier(factor: Int): (Int) -> Int {
        return { number -> number * factor }
    }

    val triple = createMultiplier(3)
    println("7 * 3 = ${triple(7)}")

    // 실전 예제: 재시도 로직
    fun <T> retry(times: Int, action: () -> T): T? {
        repeat(times) { attempt ->
            try {
                return action()
            } catch (e: Exception) {
                println("시도 ${attempt + 1} 실패: ${e.message}")
                if (attempt == times - 1) throw e
            }
        }
        return null
    }

    // 사용 예
    try {
        retry(3) {
            println("API 호출 시도...")
            if (Math.random() > 0.7) "성공" else throw Exception("네트워크 오류")
        }
    } catch (e: Exception) {
        println("최종 실패")
    }
}

// 5. Scope Functions (let, run, apply, also, with)
fun scopeFunctionsExample() {
    data class User(var name: String, var email: String)

    // let: null 체크와 변환
    var email: String? = "backend@gmail.com"
    email?.let { validEmail ->
        println("이메일 검증: $validEmail")
        // validEmail은 non-null
    }

    val length = email?.let { it.length } ?: 0
    println("이메일 길이: $length")

    // run: 객체 초기화와 결과 반환
    val user = User("홍길동", "hong@example.com")
    val message = user.run {
        name = name.uppercase()     // this 생략 가능
        email = email?.lowercase()
        "사용자 설정 완료: $name"   // 반환값
    }
    println(message)

    // apply: 객체 초기화 (객체 자체 반환)
    val newUser = User("", "").apply {
        name = "김철수"
        email = "kim@example.com"
    }
    println("새 사용자: $newUser")

    // also: 부수 효과 (로깅 등)
    val result = listOf(1, 2, 3, 4, 5)
        .filter { it > 2 }
        .also { println("필터링 결과: $it") }
        .map { it * 2 }
        .also { println("변환 결과: $it") }

    // with: 객체를 리시버로
    val numbers = mutableListOf(1, 2, 3)
    with(numbers) {
        add(4)
        add(5)
        println("최종 리스트: $this")
    }

}

// 6. 실전 예제: API 응답 처리
fun practicalExample() {
    // DTO 정의
    data class VideoResponse(
        val id: Long,
        val title: String,
        val duration: Int,
        val tags: List<String>
    )

    data class ApiResult<T>(
        val success: Boolean,
        val data: T?,
        val error: String?
    )

    // 샘플 데이터
    val responses = listOf(
        VideoResponse(1, "오징어 게임", 60, listOf("드라마", "스릴러")),
        VideoResponse(2, "더 글로리", 55, listOf("드라마", "복수")),
        VideoResponse(3, "피지컬100", 70, listOf("예능", "서바이벌")),
        VideoResponse(4, "흑백요리사", 80, listOf("예능", "요리"))
    )

    // 실전 처리 예제
    println("\n=== 드라마 필터링 ===")
    val dramas = responses
        .filter { "드라마" in it.tags }
        .also { println("드라마 개수: ${it.size}") }
        .map { it.title }
    println("드라마 목록: $dramas")

    println("\n=== 평균 러닝타임 계산 ===")
    val avgDuration = responses
        .map { it.duration }
        .average()
    println("평균: ${String.format("%.1f", avgDuration)}분")

    println("\n=== 장르별 그룹화 ===")
    val byGenre = responses
        .flatMap { video -> video.tags.map { tag -> tag to video.title } }
        .groupBy( { it.first }, { it.second })
    byGenre.forEach { (genre, titles) ->
        println("$genre, $titles")
    }

    println("\n=== API 응답 래핑 ===")
    fun <T> wrapResponse(data: T?, errorMessage: String? = null): ApiResult<T> {
        return ApiResult(
            success = data != null,
            data = data,
            error = errorMessage
        )
    }

    val successResult = wrapResponse(responses.first())
    val errorResult = wrapResponse<VideoResponse>(null, "영상을 찾을 수 없습니다")

    println("성공: ${successResult.data?.title}")
    println("실패: ${errorResult.error}")

}

// ==========================================
// 추가 학습: 확장 함수 실전 예제
// ==========================================

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

fun extensionFunctionExample() {
    val list = listOf(1, 2, 3)
    println("두 번째 요소: ${list.secondOrNull()}")

    val longTitle = "아주 긴 제목의 영상입니다"
    println("축약: ${longTitle.truncate(10)}")

}





























