package buildup
fun add(a: Int, b: Int): Int {
    return a + b
}

// 표현식 함수
fun multiply(a: Int, b: Int) = a * b

fun main() {
    println(add(3, 5))
    println(multiply(3, 5))
}