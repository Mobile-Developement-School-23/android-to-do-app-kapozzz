package com.example.todoapp.data.usecases

import android.graphics.Color
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.todoapp.data.ToDoApiService
import com.example.todoapp.data.client.DAO
import com.example.todoapp.data.model.ToDoElement
import com.example.todoapp.ioc.ToDoApplication
import com.example.todoapp.ui.model.NotificationState
import com.example.todoapp.ui.model.TodoItem
import com.example.todoapp.ui.usecases.UserNotificationHandler
import com.example.todoapp.ui.usecases.UserNotificationHandlerImpl
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject

class PosterService @Inject constructor(
    val application: ToDoApplication,
    val dao: DAO,
    val api: ToDoApiService,
    val notificationHandler: UserNotificationHandler
) {
    private var clientRevision: Int = 0

    private val _load = MutableLiveData(false)

    val load: LiveData<Boolean>
        get() = _load

    private val coroutineExceptionHandler =
        CoroutineExceptionHandler { _, throwable ->
            Log.e("Coroutine Exception Handler", "Error: ${throwable.message}")
            notificationHandler.state.value = NotificationState.Error(
                UserNotificationHandlerImpl.COROUTINE_ERROR,
                "Error: ${throwable.message}"
            )
        }

    suspend fun insertToDo(item: TodoItem) {
        if (clientRevision != getServerRevision()) merge()
        Log.d("NEW TODO", "insertToDo in Poster")
        handleApi { api.addToDo(clientRevision, ToDoElement(item)) }
    }

    suspend fun deleteToDo(item: TodoItem) {
        if (clientRevision != getServerRevision()) merge()
        handleApi { api.deleteToDo(clientRevision, item.id.toString()) }
    }

    suspend fun updateToDo(item: TodoItem) {
        if (clientRevision != getServerRevision()) merge()
        handleApi {
            api.updateToDo(
                getServerRevision(),
                item.id.toString(),
                ToDoElement(item)
            )
        }
    }

    private suspend fun getServerRevision(): Int {
        val response = api.getAllToDo()
        return response.body()?.revision ?: 0
    }

    suspend fun updateData() {
        withContext(Dispatchers.Main) {
            _load.value = true
        }

        delay(1000L)

        merge()


        withContext(Dispatchers.Main) {
            _load.value = false
        }
    }

    private suspend fun merge() {

        val serverResponse = api.getAllToDo().body()

        val serverList = serverResponse?.list?.toMutableList() ?: mutableListOf()

        clientRevision = serverResponse?.revision ?: 0

        val clientList = dao.getToDoItemsMerge()?.toMutableList() ?: mutableListOf()

        val (biggest, smaller) = if (serverList.size > clientList.size) Pair(
            serverList,
            clientList
        )
        else Pair(clientList, serverList)

        val (intersection, other) = biggest.partition { todo ->
            todo.id in smaller.flatMap {
                listOf(
                    it.id
                )
            }
        }

        intersection.map { clientToDo ->
            val serverToDo =
                serverList.find { serverToDo -> serverToDo.id == clientToDo.id }
                    ?: throw IllegalStateException("Error: invalid partition in data merge, repository")
            when {
                clientToDo.color != Color.RED.toString() && serverToDo.color != Color.RED.toString() -> {
                    clientToDo.color = Color.GREEN.toString()
                    serverToDo.color = Color.GREEN.toString()
                    when {
                        clientToDo.changed_at == serverToDo.changed_at -> dao.updateToDoItem(
                            clientToDo.apply { color = Color.GREEN.toString() })

                        clientToDo.changed_at > serverToDo.changed_at -> {
                            val actualToDo =
                                clientToDo.apply { color = Color.GREEN.toString() }
                            dao.updateToDoItem(actualToDo)
                            handleApi {
                                api.updateToDo(
                                    getServerRevision(),
                                    actualToDo.id.toString(),
                                    ToDoElement(actualToDo)
                                )
                            }
                        }

                        else -> {
                            dao.updateToDoItem(serverToDo)
                        }
                    }
                }

                else -> {
                    dao.deleteTodo(clientToDo.id.toString())
                    handleApi {
                        api.deleteToDo(
                            getServerRevision(),
                            clientToDo.id.toString()
                        )
                    }
                }
            }
        }

        val clientRemainder = mutableListOf<TodoItem>()
        val serverRemainder = mutableListOf<TodoItem>()

        other.forEach {
            if (it.id in serverList.flatMap { listOf(it.id) }) serverRemainder.add(
                it
            ) else clientRemainder.add(it)
        }

        clientRemainder.forEach {
            when {
                it.color == Color.GREEN.toString() -> dao.deleteTodo(it.id.toString())
                it.color == Color.YELLOW.toString() -> {
                    val greenToDo = it.apply { color = Color.GREEN.toString() }
                    dao.updateToDoItem(greenToDo)
                    handleApi {
                        api.addToDo(
                            getServerRevision(),
                            ToDoElement(greenToDo)
                        )
                    }
                }

                it.color == Color.RED.toString() -> dao.deleteTodo(it.id.toString())
            }
        }

        serverRemainder.forEach { dao.insertToDo(it) }
    }


    private suspend fun <T : Any> handleApi(
        task: suspend () -> Response<T>
    ) {
        val state = HandleApiImpl.execute(task)
        if (state is NotificationState.Success) ++clientRevision
        withContext(Dispatchers.Main) {
            notificationHandler.state.value = state
        }
    }
}


