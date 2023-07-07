package com.example.todoapp.data

import com.example.todoapp.data.model.ElementResponse
import com.example.todoapp.data.model.ListResponse
import com.example.todoapp.data.model.ToDoElement
import com.example.todoapp.data.model.ToDoListElement
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import javax.inject.Inject

interface ToDoApiService {
    @GET("list")
    suspend fun getAllToDo(): Response<ListResponse>

    @GET("list/{id}")
    suspend fun getToDoByID(
        @Header("X-Last-Known-Revision") revision: Int,
        @Path("id") id: String
    ): Response<ToDoElement>

    @PATCH("list")
    suspend fun updateAllToDo(
        @Header("X-Last-Known-Revision") revision: Int,
        @Body list: ToDoListElement
    ): Response<ListResponse>

    @POST("list")
    suspend fun addToDo(
        @Header("X-Last-Known-Revision") revision: Int,
        @Body element: ToDoElement
    ): Response<ElementResponse>

    @PUT("list/{id}")
    suspend fun updateToDo(
        @Header("X-Last-Known-Revision") revision: Int,
        @Path("id") id: String,
        @Body element: ToDoElement
    ): Response<ElementResponse>

    @DELETE("list/{id}")
    suspend fun deleteToDo(
        @Header("X-Last-Known-Revision") revision: Int,
        @Path("id") id: String
    ): Response<ElementResponse>
}