package com.example.todoapp.model

import java.lang.IllegalStateException


// Важность дела
enum class Importance(val level: String) {
    LOW("0"),
    BASIC("-1"),
    IMPORTANT("1");

    companion object {
        fun fromString(importance: String): Importance {
            return when(importance) {
                "LOW" -> LOW
                "BASIC" -> BASIC
                "IMPORTANT" -> IMPORTANT
                else -> throw IllegalStateException("invalid importance state in converter")
            }
        }
    }
}
