package com.example.todoapp.data.usecases

import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.todoapp.ToDoApplication

class PeriodicWork(val context: ToDoApplication, parameters: WorkerParameters): Worker(context, parameters) {
    override fun doWork(): Result {
        return try {
//            val repo = context.appComponent.
//            repo.updateDataFromServer()
            Result.success()
        } catch (e : Exception) {
            Result.failure() }
        }
    }
