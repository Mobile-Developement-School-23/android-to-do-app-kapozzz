package com.example.todoapp.data.server.retrofit

import com.example.todoapp.model.Importance
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Date
import java.util.UUID
import java.util.concurrent.TimeUnit

class ToDoRetrofit {

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val gson = GsonBuilder()
        .registerTypeAdapter(Date::class.java, DateJsonAdapter())
        .registerTypeAdapter(UUID::class.java, UUIDAdapter())
        .registerTypeAdapter(Importance::class.java, ImportanceAdapter())
        .create()

    private val okHttpClient = OkHttpClient
        .Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(TokenInterceptor())
        .build()

    private val retrofitBuilder = Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    val api = retrofitBuilder.create(ToDoApiService::class.java)

    companion object {
        private const val BASE_URL = "https://beta.mrdekk.ru/todobackend/"
    }
}