package com.example.todoapp.data.repository

import android.graphics.Color
import androidx.lifecycle.LiveData
import com.example.todoapp.data.client.ToDoDB
import com.example.todoapp.data.server.retrofit.ElementResponse
import com.example.todoapp.model.ToDoApplication
import com.example.todoapp.model.TodoItem

class RepositoryToDo private constructor(val application: ToDoApplication) {

    private val database by lazy { ToDoDB.getDatabase(application) }

    private val dao by lazy { database.mainDAO() }

    private val poster by lazy { PosterService(application) }

    val repositoryLoadData: LiveData<Boolean> = poster.load

    fun updateDataFromServer() {
        poster.updateData()
    }

    suspend fun insertTodo(item: TodoItem): NetworkResult<ElementResponse>? {
        dao.insertToDo(item).also {
            if (application.isInternetAvailable()) return poster.insertToDo(item)
        }
        return null
    }

    suspend fun updateToDo(item: TodoItem): NetworkResult<ElementResponse>? {
        dao.updateToDoItem(item).also {
            if (application.isInternetAvailable()) return poster.updateToDo(item)
        }
        return null
    }

    suspend fun deleteToDo(item: TodoItem): NetworkResult<ElementResponse>? {
        dao.updateToDoItem(item.apply { color = Color.RED.toString() }).also {
            if (application.isInternetAvailable()) return poster.updateToDo(item)
        }
        return null
    }

    fun getToDoItems(): LiveData<List<TodoItem>?> = dao.getToDoItems()

    companion object {
        private val repository: RepositoryToDo? = null

        fun getInstance(application: ToDoApplication): RepositoryToDo =
            repository ?: RepositoryToDo(application)

        const val SUCCESSFUL_OPERATION = 200
        const val CLIENT_ERROR = 400
        const val SERVER_ERROR = 500
        const val INTERNET_NOT_AVAILABLE = 911
    }
}