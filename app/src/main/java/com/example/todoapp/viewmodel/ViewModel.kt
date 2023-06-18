package com.example.todoapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.todoapp.model.RepositoryToDo
import com.example.todoapp.model.TodoItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

// Прослойка для связи UI с репозиторием
class ViewModel(application: Application) : AndroidViewModel(application) {

    private var repository: RepositoryToDo
    var allToDoItems: LiveData<List<TodoItem>?>
    var completedTaskCount = MutableLiveData<Int>()
    var savedToDoItem: TodoItem? = null
    var tasksState = false

    val job = Job()
    val scope = CoroutineScope(Dispatchers.IO + job)

    val observer = Observer<List<TodoItem>?> { list ->
        var counter = 0
        completedTaskCount.value = if (list.isNullOrEmpty()) 0
        else {
            for (task in list) {
                if (task.isCompleted) ++counter
            }
            counter
        }
    }

    init {
        repository = RepositoryToDo.getInstance(application.applicationContext)
        allToDoItems = repository.getToDoItems()
        allToDoItems.observeForever(observer)
    }

    fun insertToDo(newToDo: TodoItem) {
        scope.launch {
            repository.insertToDo(newToDo)
        }
    }

    fun updateToDo(todoItem: TodoItem) {
        scope.launch {
            repository.updateToDo(todoItem)
        }
    }

    fun deleteToDo(itemID: String) {
        scope.launch {
            repository.deleteToDo(itemID)
        }
    }

    fun generateRandomTodoItems() {
        repository.generateTodoItems()
    }

    override fun onCleared() {
        super.onCleared()
        allToDoItems.removeObserver(observer)
        job.cancel()
    }
}