package buildup
fun main() {
    val name: String = "Jonathan"           // 변경 불가 (상수), Java의 final
    var age: Int = 25                       // 변경 가능 (변수), 타입 추론 가능 -> val name = "Jonathan" 이렇게 서도 됨

    println("이름: $name, 나이: $age")
    age = 26
    println("변경된 나이: $age")
}