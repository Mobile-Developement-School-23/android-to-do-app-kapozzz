package com.example.todoapp.data.usecases

import com.example.todoapp.ui.model.NotificationState
import com.example.todoapp.ui.usecases.UserNotificationHandlerImpl
import retrofit2.HttpException
import retrofit2.Response

object HandleApiImpl: HandleApi {
    override suspend fun <T : Any> execute(task: suspend () -> Response<T>): NotificationState<Any> {
        val state: NotificationState<Any>? = try {
            val response = task.invoke()
            val body = response.body()
            if (response.isSuccessful && body != null) {
                NotificationState.Success(body)
            } else {
                NotificationState.Error(code = response.code(), message = response.message())
            }
        } catch (e: HttpException) {
            NotificationState.Error(code = e.code(), message = e.message())
        } catch (e: Throwable) {
            NotificationState.Exception(e)
        }
        return state ?: NotificationState.Error(
            UserNotificationHandlerImpl.CLIENT_ERROR,
            "Error: null state in handle api"
        )
    }
}