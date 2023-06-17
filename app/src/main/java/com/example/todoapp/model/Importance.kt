package com.example.todoapp.model


// Важность дела
enum class Importance(val level: String) {
    LOW("-1"),
    URGENT("1");

    companion object {
        fun fromString(importance: String): Importance? {
            return when(importance) {
                "LOW" -> Importance.LOW
                "URGENT" -> Importance.URGENT
                else -> null
            }
        }
    }
}
