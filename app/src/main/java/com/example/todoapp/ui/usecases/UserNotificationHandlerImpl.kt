package com.example.todoapp.ui.usecases

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.todoapp.R
import com.example.todoapp.ioc.ToDoApplication
import com.example.todoapp.ui.model.NotificationState

class UserNotificationHandlerImpl private constructor() : UserNotificationHandler {

    override val state: MutableLiveData<NotificationState<Any>> = MutableLiveData()

    override val message: MutableLiveData<String> = MutableLiveData()

    private var application: ToDoApplication

    private val observer = Observer<NotificationState<Any>> { state ->
        handleState(state)
        Log.i("UserNotificationHandler", "New state - ${state}")
    }

    init {
        state.value = NotificationState.Success("handler init complete")
        Log.i("UserNotificationHandler", "Info: handler init complete")
        application = ToDoApplication.getInstance()
        initObserver()
    }

    private fun initObserver() {
        state.observeForever(observer)
    }

    private fun handleState(state: NotificationState<Any>) {

        when (state) {
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

    companion object {
        const val SERVER_ERROR = 500
        const val CLIENT_ERROR = 400
        const val COROUTINE_ERROR = 600

        var notHandler: UserNotificationHandlerImpl? = null
        val lock = Any()

        fun getInstance(): UserNotificationHandlerImpl {
            if (notHandler == null) {
                synchronized(lock) {
                    if (notHandler == null) {
                        notHandler = UserNotificationHandlerImpl()
                    }
                }
            }
            return notHandler!!
        }
    }
}