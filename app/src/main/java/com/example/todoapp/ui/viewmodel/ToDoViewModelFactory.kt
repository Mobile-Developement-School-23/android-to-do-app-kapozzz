package com.example.todoapp.ui.viewmodel

import android.net.ConnectivityManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.todoapp.data.RepositoryToDo
import com.example.todoapp.ToDoApplication
import com.example.todoapp.utils.NetworkConnectivityStatus
import javax.inject.Inject

class ToDoViewModelFactory @Inject constructor(
    val application: ToDoApplication,
    val repositoryToDo: RepositoryToDo,
    val internetStatus: NetworkConnectivityStatus
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ToDoViewModel(application, repositoryToDo, internetStatus) as T
    }
}