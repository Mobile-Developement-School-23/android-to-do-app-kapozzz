package com.example.todoapp.data

import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.todoapp.data.client.DAO
import com.example.todoapp.data.model.NetworkResult
import com.example.todoapp.data.usecases.PeriodicWork
import com.example.todoapp.data.usecases.PosterService
import com.example.todoapp.data.model.ElementResponse
import com.example.todoapp.ioc.ToDoApplication
import com.example.todoapp.ui.model.TodoItem
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class RepositoryToDo @Inject constructor(
    val application: ToDoApplication,
    val dao: DAO,
    val poster: PosterService
) {

    val repositoryLoadData: LiveData<Boolean> = poster.load

    init {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workPeriodicRequest = PeriodicWorkRequestBuilder<PeriodicWork>(8, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(application).enqueue(workPeriodicRequest)
    }

    fun updateDataFromServer() {
        poster.updateData()
    }

    suspend fun insertTodo(item: TodoItem): NetworkResult<ElementResponse>? {
        dao.insertToDo(item).also {
            if (application.isInternetAvailableAppField()) return poster.insertToDo(item)
        }
        return null
    }

    suspend fun updateToDo(item: TodoItem): NetworkResult<ElementResponse>? {
        dao.updateToDoItem(item).also {
            if (application.isInternetAvailableAppField()) return poster.updateToDo(item)
        }
        return null
    }

    suspend fun deleteToDo(item: TodoItem): NetworkResult<ElementResponse>? {
        dao.updateToDoItem(item.apply { color = Color.RED.toString() }).also {
            if (application.isInternetAvailableAppField()) return poster.updateToDo(item)
        }
        return null
    }

    fun getToDoItems(): LiveData<List<TodoItem>?> = dao.getToDoItems()
}