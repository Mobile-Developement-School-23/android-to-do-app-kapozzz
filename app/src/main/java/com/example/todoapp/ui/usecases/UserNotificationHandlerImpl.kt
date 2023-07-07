package com.example.todoapp.ui.usecases

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.todoapp.R
import com.example.todoapp.ioc.ToDoApplication
import com.example.todoapp.ui.model.NotificationState

object UserNotificationHandlerImpl : UserNotificationHandler {

    override val state: MutableLiveData<NotificationState<Any>> = MutableLiveData()

    override val message: MutableLiveData<String> = MutableLiveData()

    private val application = ToDoApplication.getInstance()

    init {
        state.value = NotificationState.Success("handler init complete")
        Log.i("UserNotificationHandler", "Info: handler init complete")
    }

    private val observer = Observer<NotificationState<Any>> {
        handleState(it)
    }

    private fun initObserver() {
        state.observeForever(observer)
    }

    private fun handleState(state: NotificationState<Any>) {

        when(state) {
            is NotificationState.Success -> {
                return
            }

            is NotificationState.Error -> {
                message.value = application.getString(R.string.error400)
            }

            else -> {
                message.value = application.getString(R.string.incorrect_operation)
            }
        }
    }

    fun destroyObserver() {
        state.removeObserver(observer)
    }

}