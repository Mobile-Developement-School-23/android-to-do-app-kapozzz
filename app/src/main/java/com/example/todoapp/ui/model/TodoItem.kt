package com.example.todoapp.ui.model

import android.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.Date
import java.util.UUID

@Entity(tableName = "TodoItems")
data class TodoItem(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    var text: String,
    var importance: Importance,
    var deadline: Date?,
    var done: Boolean,
    var color: String = Color.YELLOW.toString(),
    val created_at: Date,
    var changed_at: Long,
    val last_updated_by: String = "kapozzz"
) : Serializable


