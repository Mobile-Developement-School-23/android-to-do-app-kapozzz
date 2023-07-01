package com.example.todoapp.data.repository

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.todoapp.model.ToDoApplication

class PeriodicWork(val context: Context, parameters: WorkerParameters): Worker(context, parameters) {
    override fun doWork(): Result {
        return try {
            val repo = RepositoryToDo.getInstance(context.applicationContext as ToDoApplication)
            repo.updateDataFromServer()
            Result.success()
        } catch (e : Exception) {
            Result.failure() }
        }
    }
