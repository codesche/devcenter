package exercise

import kotlin.runCatching

fun main() {
    println("=== 1. 예외 처리 ===")
    exceptionHandling()

    println("\n=== 2. Null 안전성 ===")
    nullSafety()

    println("\n=== 3. Result 타입 ===")
    resultTypeExample()

    println("\n=== 4. 실전: REST API 시뮬레이션 (동기 버전) ===")
    restApiSimulation()
}

// 1. 예외 처리
fun exceptionHandling() {
    // 기본 try-catch
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

    println("10 / 2 = ${divide(10, 2)}")
    println("10 / 0 = ${divide(10, 0)}")

    // 커스텀 예외
    class VideoNotFoundException(message: String) : Exception(message)

    fun findVideo(id: Long): String {
        if (id <= 0) {
            throw VideoNotFoundException("영상 ID가 유효하지 않습니다: $id")
        }
        return "Video-$id"
    }

    try {
        println(findVideo(1))
        println(findVideo(2))
    } catch (e: VideoNotFoundException) {
        println("커스텀 에러: ${e.message}")
    }

    // runCatching: 예외를 Result로 변환
    val result1 = runCatching { divide(10, 2) }
    val result2 = runCatching { divide(10, 0) }

    println("runCatching 성공: ${result1.getOrNull()}")
    println("runCatching 실패: ${result2.exceptionOrNull()?.message}")

}

// 2. Null 안전성
fun nullSafety() {
    // Safe call 연산자 ?.
    val name: String? = "TVING"
    println("길이: ${name?.length}")

    val nullName: String? = null
    println("null 길이: ${nullName?.length}")     // null 반환

    // Elvis 연산자 ?:
    val length = nullName?.length ?: 0
    println("Elvis 연산자: $length")

    // let과 조합
    name?.let {
        println("이름이 있음: $it")
    }

    nullName?.let {
        println("이 줄은 실행되지 않음")
    } ?: println("null이므로 이 줄 실행")

    // 안전한 캐스팅 as?
    val obj: Any = "문자열"
    val str: String? = obj as? String
    val num: Int? = obj as? Int
    println("문자열 캐스팅: $str")
    println("숫자 캐스팅: $num")

    // requireNotNull, checkNotNull
    fun processUser(name: String?) {
        val validName = requireNotNull(name) { "이름은 필수입니다" }
        println("처리: $validName")
    }

    try {
        processUser("홍길동")
        processUser(null)
    } catch (e: IllegalArgumentException) {
        println("에러: ${e.message}")
    }

    println("!! 연산자는 사용하지 않기!!!")
}

// 3. Result 타입
fun resultTypeExample() {
    // Result를 반환하는 함수
    fun divideWithResult(a: Int, b: Int): Result<Int> {
        return if (b == 0) {
            Result.failure(ArithmeticException("0으로 나눌 수 없습니다"))
        } else {
            Result.success(a / b)
        }
    }

    // Result 처리 방법 1: getOrNull
    val result1 = divideWithResult(10, 2)
    println("결과: ${result1.getOrNull()}")

    val result2 = divideWithResult(10, 0)
    println("결과: ${result2.getOrNull()}")

    // Result 처리 방법 2: getOrDefault
    val result3 = divideWithResult(10, 0).getOrDefault(0)
    println("기본값 사용: $result3")

    // Result 처리 방법 3: fold
    divideWithResult(10, 0).fold(
        onSuccess = { println("성공: $it") },
        onFailure = { println("실패: ${it.message}") }
    )

    divideWithResult(10, 0).fold(
        onSuccess = { println("성공: $it") },
        onFailure = { println("실패: ${it.message}") }
    )

    // Result 체이닝
    val finalResult = divideWithResult(100, 2)
        .map { it * 2 }
        .map { it + 10 }
        .getOrDefault(0)

    println("체이닝 결과: $finalResult")
}

// 4. 실전 REST API 시뮬레이션
fun restApiSimulation() {
    // DTO 정의
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

    // Service Layer 시뮬레이션
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
                    createdAt = "2025-10-04"
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

    // Controller Layer 시뮬레이션
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

    // 실행
    val service = VideoService()
    val controller = VideoController(service)

    println("\n[POST /api/videos]")
    val createResponse1 = controller.createVideo(
        VideoRequest("오징어 게임", 60)
    )
    println("응답: ${createResponse1.data?.title} (ID: ${createResponse1.data?.id})")

    println("\n[POST /api/videos]")
    val createResponse2 = controller.createVideo(
        VideoRequest("더 글로리", 55)
    )
    println("응답: ${createResponse2.data?.title} (ID: ${createResponse2.data?.id})")

    println("\n[POST /api/videos]")
    val createResponse3 = controller.createVideo(
        VideoRequest("피지컬100", 70)
    )
    println("응답: ${createResponse3.data?.title} (ID: ${createResponse3.data?.id})")

    println("\n[GET /api/videos/1]")
    val getResponse = controller.getVideo(1)
    println("응답: ${getResponse.data}")

    println("\n[GET /api/videos]")
    val getAllResponse = controller.getAllVideos()
    println("전체 영상 개수: ${getAllResponse.data?.size}")
    getAllResponse.data?.forEach { video ->
        println("  - ${video.title} (${video.duration}분)")
    }

    println("\n[GET /api/videos/999]")
    val notFoundResponse = controller.getVideo(999)
    println("응답: success=${notFoundResponse.success}, error=${notFoundResponse.error}")

    println("\n[POST /api/videos - 빈 제목]")
    val invalidResponse = controller.createVideo(
        VideoRequest("", 60)
    )
    println("응답: success=${invalidResponse.success}, error=${invalidResponse.error}")
}




















