package oop

open class Animal {
    open fun sound() {}
}

class Dog: Animal() {
    override fun sound() = println("Woof")
}