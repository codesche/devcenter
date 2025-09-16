package kotlingrammar

import oop.User

fun main() {
    var name: String? = null
    println(name?.length)       // 안전 호출

    // Elvis 연산자
    val len = name?.length ?: 0

    // 람다식
    val sum = { x: Int, y: Int -> x + y }

    // 컬렉션
    val nums = listOf(1, 2, 3)
    val doubled = nums.map { it * 2}

    // mutableList
    val items = mutableListOf("A", "B")
    items.add("C")

    // set & map
    val set = setOf(1, 2, 2, 3)
    val map = mapOf("key" to "value")

    // Destructuring
    val (id, username) = User(1, "James")
    println("$id - $username")

    println("Hello".addKotlin())
    println(operate(3, 5) {a, b -> a + b})
}

// 스마트 캐스트
fun printLength(obj: Any) {
    if (obj is String) {
        println(obj.length)
    }
}

// 확장 함수
fun String.addKotlin() = this + " Kotlin"

// 고차 함수
fun operate(x: Int, y: Int, op: (Int, Int) -> Int) = op(x, y)



