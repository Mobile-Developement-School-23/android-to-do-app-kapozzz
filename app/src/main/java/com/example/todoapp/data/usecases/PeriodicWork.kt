package com.example.todoapp.data.usecases

import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.todoapp.ioc.ToDoApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PeriodicWork(val context: ToDoApplication, parameters: WorkerParameters): Worker(context, parameters) {

    override fun doWork(): Result {
        return try {
            val repo = context.applicationComponent?.getRepositoryInstance()
            repo?.let {repositoryToDo ->
             CoroutineScope(Dispatchers.IO).launch { repositoryToDo.updateDataFromServer() }
            }
            Result.success()
        } catch (e : Exception) {
            Result.failure() }
        }
}
