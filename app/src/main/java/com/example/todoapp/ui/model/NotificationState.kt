package com.example.todoapp.ui.model


sealed class NotificationState<T : Any> {

    data class Success<T : Any>(val data: T) : NotificationState<T>()

    data class Error<T: Any>(val code: Int, val message: String?): NotificationState<T>()

    data class CoroutineError<T: Any>(val message: String?): NotificationState<T>()

    data class Exception<T: Any>(val e: Throwable): NotificationState<T>()

}