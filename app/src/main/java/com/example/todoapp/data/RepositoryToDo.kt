package com.example.todoapp.data

import android.graphics.Color
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.todoapp.data.client.DAO
import com.example.todoapp.data.usecases.PeriodicWork
import com.example.todoapp.data.usecases.PosterService
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

    suspend fun updateDataFromServer() {
        poster.updateData()
    }

    suspend fun insertTodo(item: TodoItem) {
        dao.insertToDo(item).also {
            if (application.isInternetAvailableAppField()) poster.insertToDo(item)
            Log.d("NEW TODO", "insertToDo in rep with internet")
        }
    }

    suspend fun updateToDo(item: TodoItem) {
        dao.updateToDoItem(item).also {
            if (application.isInternetAvailableAppField()) poster.updateToDo(item)
        }
    }

    suspend fun deleteToDo(item: TodoItem) {
        dao.updateToDoItem(item.apply { color = Color.RED.toString() }).also {
            if (application.isInternetAvailableAppField()) poster.updateToDo(item)
        }
    }

    fun getToDoItems(): LiveData<List<TodoItem>?> = dao.getToDoItems()
}