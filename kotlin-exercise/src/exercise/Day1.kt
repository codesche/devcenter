package exercise

// 실습 모음
fun main() {
    println("=== 1. 변수 선언 ===")
    variableExample()

    println("\n=== 2. 함수 기본 ===")
    functionExample()

    println("\n=== 3. 조건문 ===")
    conditionalExample()

    println("\n=== 4. 반복문 ===")
    loopExample()

    println("\n=== 5. 클래스 기본 ===")
    classExample()

    bonusExample()
}

// 1. 변수 선언 (val vs var)
fun variableExample() {
    // val: 읽기 전용 (Java의 final과 유사)
    val name: String = "TVING"
    val userCount = 1000000         // 타입 추론

    // var: 변경 가능한 변수
    var currentUsers = 50000
    currentUsers = 60000        // 재할당 가능

    // nullable 타입
    var optionalName: String? = null
    optionalName = "Backend Engineer"

    println("서비스명: $name")
    println("총 사용자: $userCount")
    println("현재 접속자: $currentUsers")
    println("직무: $optionalName")
}

// 2. 함수 선언
fun functionExample() {
    // 기본 함수
    fun greet(name: String): String {
        return "안녕하세요, $name 님!"
    }

    // 단일 표현식 함수
    fun add(a: Int, b: Int) = a + b

    // 기본 매개변수
    fun createUser(name: String, age: Int = 20) {
        println("사용자 생성: $name (나이: $age)")
    }

    println(greet("개발자"))
    println("5 + 3 = ${add(5, 3)}")
    createUser("홍길동")
    createUser("김철수", 25)
}

// 3. 조건문 (if, when)
fun conditionalExample() {
    val score = 84

    // if는 표현식 (값을 반환)
    val grade = if (score >= 90) {
        "A"
    } else if (score >= 80) {
        "B"
    } else {
        "C"
    }

    println("점수: $score, 학점: $grade")

    // when 표현식 (Java의 switch보다 강력)
    val day = 3
    val dayName = when (day) {
        1 -> "월요일"
        2 -> "화요일"
        3 -> "수요일"
        in 4..5 -> "목/금요일"
        else -> "주말"
    }

    println("요일: $dayName")
}

// 4. 반복문
fun loopExample() {
    // for 루프
    println("for 루프: ")
    for (i in 1..3) {
        println(" 반복 $i")
    }

    // 리스트 순회
    val videos = listOf("드라마", "예능", "영화")
    println("\n컨텐츠 목록: ")
    for (video in videos) {
        println(" - $video")
    }

    // 인덱스와 함께 순회
    println("\n 인덱스와 함께: ")
    for ((index, video) in videos.withIndex()) {
        println(" ${index + 1}. $video")
    }

    // while 루프
    var count = 0
    while (count < 3) {
        println(" count: $count")
        count++
    }

}

// 5. 클래스 기본
fun classExample() {
    // 기본 클래스
    class Video(val title: String, val duration: Int) {
        fun play() {
            println("재생 중: $title (${duration}분)")
        }
    }

    // 데이터 클래스 (자동으로 equals, hashCode, toString 생성)
    data class User(
        val id: Int,
        val name: String,
        val isPremium: Boolean = false
    )

    // 객체 생성 및 사용
    val video1 = Video("응답하라 1988", 90)
    video1.play()

    val user1 = User(1, "홍길동", true)
    val user2 = User(2, "김철수")

    println("\n사용자 정보: ")
    println(" $user1")
    println(" $user2")

    // 데이터 클래스의 copy 기능
    val user3 = user2.copy(name = "이영희")
    println(" $user3")

}

// ==========================================
// 보너스: 실전 예제
// ==========================================

// Sealed Class (제한된 클래스 계층)
sealed class VideoState {
    object Loading : VideoState()
    data class Success(val title: String) : VideoState()
    data class Error(val message: String) : VideoState()
}

fun handleVideoState(state: VideoState) {
    when (state) {
        is VideoState.Loading -> println("로딩 중...")
        is VideoState.Success -> println("재생: ${state.title}")
        is VideoState.Error -> println("오류: ${state.message}")
    }
}

// Extension Function (확장 함수)
fun String.isValidEmail(): Boolean {
    return this.contains("@") && this.contains(".")
}

fun bonusExample() {
    println("\n=== 보너스 예제 ===")

    // Sealed Class 사용
    val state1 = VideoState.Loading
    val state2 = VideoState.Success("오징어 게임")
    handleVideoState(state1)
    handleVideoState(state2)

    // Extension Function 사용
    val email = "developer@tving.com"
    println("\n이메일 검증: $email -> ${email.isValidEmail()}")
}
