package oop

sealed class Result

class Success(val data: String): Result()
class Error(val msg: String): Result()