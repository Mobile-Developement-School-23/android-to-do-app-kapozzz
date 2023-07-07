package com.example.todoapp.ui.model

import java.lang.IllegalStateException

enum class Importance(val level: String) {
    LOW("low"),
    BASIC("basic"),
    IMPORTANT("important");

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
