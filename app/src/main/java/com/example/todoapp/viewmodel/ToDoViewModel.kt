package com.example.todoapp.viewmodel

import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.todoapp.data.RepositoryToDo
import com.example.todoapp.ioc.ToDoApplication
import com.example.todoapp.ui.model.Importance
import com.example.todoapp.ui.model.NotificationState
import com.example.todoapp.ui.model.TodoItem
import com.example.todoapp.ui.usecases.UserNotificationHandler
import com.example.todoapp.ui.usecases.UserNotificationHandlerImpl
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.Date

class ToDoViewModel(
    val application: ToDoApplication,
    val repository: RepositoryToDo,
    val notificationHandler: UserNotificationHandler
) : AndroidViewModel(application) {

    private val scope = scopeConstructor()

    var progressBarIsActive: LiveData<Boolean> = repository.repositoryLoadData

    var allToDoItems: LiveData<List<TodoItem>?> = repository.getToDoItems()

    var completedTaskCount = MutableLiveData<Int>()

    var savedToDoItem: TodoItem? = null

    var snackbarWithError: ((message: String) -> Unit)? = null

    var changeDeadline: Date? = null

    var changeImportance: Importance = Importance.LOW

    private val completedTaskObserver = Observer<List<TodoItem>?> { list ->
        var counter = 0
        completedTaskCount.value = if (list.isNullOrEmpty()) 0
        else {
            for (task in list) {
                if (task.done) ++counter
            }
            counter
        }
    }

    private val notificationMessageObserver = Observer<String> { message ->
        snackbarWithError?.invoke(message) ?: Log.e(
            "UserNotificationHandler",
            "Error: view model snackbar is null"
        )
    }

    init {
        allToDoItems.observeForever(completedTaskObserver)
        notificationHandler.message.observeForever(notificationMessageObserver)
    }

    fun insertToDo(newToDo: TodoItem) {
        scope.launch {
            repository.insertTodo(newToDo)
            Log.d("NEW TODO", "insertToDo in ViewModel")
        }
    }

    fun updateToDo(todoItem: TodoItem) {
        scope.launch {
            repository.updateToDo(todoItem)
        }
    }

    fun deleteToDo(todoItem: TodoItem) {
        scope.launch {
            repository.deleteToDo(todoItem)
        }
    }

    fun refreshList() {
        scope.launch {
            repository.updateDataFromServer()
        }
    }

    private fun scopeConstructor(): CoroutineScope {
        val coroutineExceptionHandler =
            CoroutineExceptionHandler { _, throwable ->
                Log.e("Coroutine Exception Handler", "Error: ${throwable.message}")
                notificationHandler.state.value = NotificationState.Error(
                    UserNotificationHandlerImpl.COROUTINE_ERROR,
                    "Error: ${throwable.message}"
                )
            }

        return CoroutineScope(Dispatchers.IO + coroutineExceptionHandler + CoroutineName("ViewModelCustomScope"))
    }

    override fun onCleared() {
        super.onCleared()
        allToDoItems.removeObserver(completedTaskObserver)
        notificationHandler.message.removeObserver(notificationMessageObserver)
        scope.cancel()
    }
}