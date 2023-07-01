package com.example.todoapp.data.client

import androidx.room.TypeConverter
import com.example.todoapp.model.Importance
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class TypeConverter {
    @TypeConverter
    fun toUUID(uuid: String?): UUID? {
        return UUID.fromString(uuid)
    }

    @TypeConverter
    fun fromUUID(uuid: UUID?): String? {
        return uuid?.toString()
    }

    @TypeConverter
    fun fromImportance(importance: Importance?): String? {
        return importance?.toString()
    }

    @TypeConverter
    fun toImportance(importance: String?): Importance? {
        return importance?.let { Importance.fromString(it) }
    }

    @TypeConverter
    fun fromDate(date: Date?): String? {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale("ru", "RU"))
        return date?.let { dateFormat.format(it) }
    }

    @TypeConverter
    fun toDate(date: String?): Date? {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale("ru", "RU"))
        return date?.let { dateFormat.parse(it) }
    }
}