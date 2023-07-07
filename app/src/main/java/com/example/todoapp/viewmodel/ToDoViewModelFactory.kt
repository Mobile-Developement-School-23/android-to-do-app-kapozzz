package com.example.todoapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.todoapp.data.RepositoryToDo
import com.example.todoapp.ioc.ToDoApplication
import javax.inject.Inject

class ToDoViewModelFactory @Inject constructor(
    val application: ToDoApplication,
    val repositoryToDo: RepositoryToDo) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ToDoViewModel(application, repositoryToDo) as T
    }
}