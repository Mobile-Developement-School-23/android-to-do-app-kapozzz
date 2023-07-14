package com.example.todoapp.utils

import androidx.lifecycle.MutableLiveData
import com.example.todoapp.ui.model.NotificationState

interface UserNotificationHandler {
    val state: MutableLiveData<NotificationState<Any>>
    val message: MutableLiveData<String>
}

