package com.example.todoapp.viewmodel

import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.todoapp.R
import com.example.todoapp.data.model.NetworkResult
import com.example.todoapp.data.RepositoryToDo
import com.example.todoapp.data.model.ElementResponse
import com.example.todoapp.ioc.ToDoApplication
import com.example.todoapp.ui.model.TodoItem
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ToDoViewModel(val application: ToDoApplication, val repository: RepositoryToDo) : AndroidViewModel(application) {

    private val scope = scopeConstructor()

    var progressBarIsActive: LiveData<Boolean> = repository.repositoryLoadData

    var allToDoItems: LiveData<List<TodoItem>?> = repository.getToDoItems()

    var completedTaskCount = MutableLiveData<Int>()

    var savedToDoItem: TodoItem? = null

    var snackbarWithError: ((message: Int) -> Unit)? = null

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

    fun refreshList() { repository.updateDataFromServer() }

    private fun scopeConstructor(): CoroutineScope {
        val coroutineExceptionHandler =
            CoroutineExceptionHandler { _, throwable ->
                Log.e("Coroutine Exception Handler", "Error: ${throwable.message}")
                snackbarWithError?.let { it(R.string.incorrect_operation) }
            }
        return CoroutineScope(Dispatchers.IO + coroutineExceptionHandler + CoroutineName("ViewModelCustomScope"))
    }

    private suspend fun handlingOperation(result: NetworkResult<ElementResponse>?) {
        withContext(Dispatchers.Main) {
            if (result != null) {
                when (result) {
                    is NetworkResult.Error -> {
                        snackbarWithError?.let { it(R.string.invalid_server) }
                        Log.i(
                            "Network result",
                            "State: Error(invalid server); message: ${result.message}; code: ${result.code}"
                        )
                    }

                    is NetworkResult.Exception -> {
                        snackbarWithError?.let { it(R.string.invalid_client) }
                        Log.i(
                            "Network Result",
                            "State: Exception(invalid client); message: ${result.e}"
                        )
                    }

                    else -> Log.i("Network Result", "State: Successful")
                }
            } else Log.i("Network Result", "State: internet is not available")
        }
    }

    override fun onCleared() {
        super.onCleared()
        allToDoItems.removeObserver(observer)
        scope.cancel()
    }
}