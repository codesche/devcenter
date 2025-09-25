class UserEmail {
}

// ❌ 나쁜 예 - 변수 스코프가 불필요하게 넓음
fun findUserEmail(userId: Long): String? {
    var user: User? = null  // 너무 일찍 선언
    var email: String? = null

    user = userRepository.findById(userId)
    if (user != null) {
        email = user.email
        if (email.isNotEmpty()) {
            return email
        }
    }
    return null
}

// ✅ 좋은 예 - 스코프 최소화
fun findUserEmail(userId: Long): String? {
    val user = userRepository.findById(userId) ?: return null
    val email = user.email
    return if (email.isNotEmpty()) email else null
}

// 🚀 더 좋은 예 - 코틀린다운 방식
fun findUserEmail(userId: Long): String? {
    return userRepository.findById(userId)
        ?.email
        ?.takeIf { it.isNotEmpty() }
}