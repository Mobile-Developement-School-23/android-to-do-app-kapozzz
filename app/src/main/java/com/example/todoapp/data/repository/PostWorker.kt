package com.example.todoapp.data.repository

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.lang.Exception

class PostWorker(context: Context, params: WorkerParameters): Worker(context, params) {
    override fun doWork(): Result {
        try {

        } catch (e: Exception) {
            return Result.failure()
        }
        return Result.success()
    }
}