package oop

interface Drivable {
    fun drive()
}

class Bike: Drivable {
    override fun drive() = println("Go!")
}