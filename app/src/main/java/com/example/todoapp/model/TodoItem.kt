package com.example.todoapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.Date
import java.util.UUID

@Entity(tableName = "TodoItems")
data class TodoItem(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    var text: String,
    var importance: Importance?,
    var deadline: Date?,
    var isCompleted: Boolean,
    val creationDate: Date
) : Serializable


