package com.example.todoapp.data.usecases

import com.example.todoapp.ui.model.Importance
import com.google.gson.GsonBuilder
import java.util.Date
import java.util.UUID
import javax.inject.Inject

class Gson  @Inject constructor(dateAdapter: DateJsonAdapter, uuidAdapter: UUIDAdapter, importanceAdapter: ImportanceAdapter) {

    val getGson = GsonBuilder()
        .registerTypeAdapter(Date::class.java, dateAdapter)
        .registerTypeAdapter(UUID::class.java, uuidAdapter)
        .registerTypeAdapter(Importance::class.java, importanceAdapter)
        .create()

}