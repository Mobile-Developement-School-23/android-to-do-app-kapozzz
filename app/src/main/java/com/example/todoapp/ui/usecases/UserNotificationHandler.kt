package com.example.todoapp.ui.usecases

import androidx.lifecycle.MutableLiveData
import com.example.todoapp.ui.model.NotificationState

interface UserNotificationHandler {
    val state: MutableLiveData<NotificationState<Any>>
    val message: MutableLiveData<String>
}

