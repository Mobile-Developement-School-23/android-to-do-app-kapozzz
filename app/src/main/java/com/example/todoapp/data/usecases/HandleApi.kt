package com.example.todoapp.data.usecases

import android.telephony.AccessNetworkConstants
import com.example.todoapp.ui.model.NotificationState
import retrofit2.Response

interface HandleApi {
    suspend fun <T : Any> execute(task : suspend () -> Response<T>): NotificationState<Any>
}