package com.example.todoapp.data.usecases

import com.example.todoapp.ui.model.Importance
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type
import java.util.Date
import java.util.UUID
import javax.inject.Inject

class UUIDAdapter @Inject constructor() : JsonSerializer<UUID>, JsonDeserializer<UUID> {
    override fun serialize(
        src: UUID?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return JsonPrimitive(src.toString())
    }

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): UUID {
        return UUID.fromString(json?.asString)
    }
}

class ImportanceAdapter @Inject constructor() : JsonSerializer<Importance>,
    JsonDeserializer<Importance> {
    override fun serialize(
        src: Importance?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return JsonPrimitive(src?.level ?: throw IllegalStateException())
    }

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Importance {
        val level = json?.asString
        return when (level) {
            "low" -> Importance.LOW
            "basic" -> Importance.BASIC
            "important" -> Importance.IMPORTANT
            else -> throw JsonParseException("Unknown importance level: $level")
        }
    }
}

class DateJsonAdapter @Inject constructor() : JsonSerializer<Date>, JsonDeserializer<Date> {
    override fun serialize(
        src: Date,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement {
        return JsonPrimitive(src.time / 1000)
    }

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): Date {
        return Date(json.asLong * 1000)
    }
}

