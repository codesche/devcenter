package buildup
// when - switch 대체
fun main() {
    val day = 3

    val result = when(day) {
        1 -> "월요일"
        2 -> "화요일"
        3 -> "수요일"
        else -> "기타 요일"
    }

    println(result)
}