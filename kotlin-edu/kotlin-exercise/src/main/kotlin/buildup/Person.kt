package buildup

class Person(val name: String, var age: Int)

fun main() {
    val person = Person("Minsung", 28)
    println("이름: ${person.name}, 나이: ${person.age}")

    person.age = 29
    println("변경된 나이: ${person.age}")
}