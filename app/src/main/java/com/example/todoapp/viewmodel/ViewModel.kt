package com.example.todoapp.viewmodel

import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.todoapp.data.repository.NetworkResult
import com.example.todoapp.data.repository.RepositoryToDo
import com.example.todoapp.data.server.retrofit.ElementResponse
import com.example.todoapp.model.ToDoApplication
import com.example.todoapp.model.TodoItem
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ViewModel(val application: ToDoApplication) : AndroidViewModel(application) {

    private var repository: RepositoryToDo = RepositoryToDo.getInstance(application)

    private val coroutineExceptionHandler =
        CoroutineExceptionHandler { coroutineContext, throwable ->
            Log.e("Coroutine Exception Handler", "Error: ${throwable.message}")
        }

    private val job = Job()

    private val scope =
        CoroutineScope(Dispatchers.IO + job + coroutineExceptionHandler + CoroutineName("ViewModelCustomScope"))

    var progressBarIsActive: LiveData<Boolean> = repository.repositoryLoadData

    var allToDoItems: LiveData<List<TodoItem>?> = repository.getToDoItems()

    var completedTaskCount = MutableLiveData<Int>()

    var savedToDoItem: TodoItem? = null

    var tasksState = false


    private val _code = MutableLiveData<NetworkResult<ElementResponse>?>()
        .apply { NetworkResult.Success("OK") }
    val networkResult: LiveData<NetworkResult<ElementResponse>?>
        get() = _code

    private val observer = Observer<List<TodoItem>?> { list ->
        var counter = 0
        completedTaskCount.value = if (list.isNullOrEmpty()) 0
        else {
            for (task in list) {
                if (task.done) ++counter
            }
            counter
        }
    }

    init {
        allToDoItems.observeForever(observer)
    }

    fun insertToDo(newToDo: TodoItem) {
        scope.launch {
            val networkResult = repository.insertTodo(newToDo)
            handlingOperation(networkResult)
        }
    }

    fun updateToDo(todoItem: TodoItem) {
        scope.launch {
            val networkResult = repository.updateToDo(todoItem)
            handlingOperation(networkResult)
        }
    }

    fun deleteToDo(todoItem: TodoItem) {
        scope.launch {
            val networkResult = repository.deleteToDo(todoItem)
            handlingOperation(networkResult)
        }
    }

    fun updateDataFromServer() {
        repository.updateDataFromServer()
    }

    private suspend fun handlingOperation(networkResult: NetworkResult<ElementResponse>?) {
        withContext(Dispatchers.Main) {
            _code.value = networkResult
        }
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
        allToDoItems.removeObserver(observer)
    }
}