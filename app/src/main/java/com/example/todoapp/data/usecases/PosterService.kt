package com.example.todoapp.data.usecases

import android.graphics.Color
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.todoapp.data.client.DAO
import com.example.todoapp.data.model.ElementResponse
import com.example.todoapp.data.model.NetworkResult
import com.example.todoapp.data.model.ToDoElement
import com.example.todoapp.data.ToDoApiService
import com.example.todoapp.ioc.ToDoApplication
import com.example.todoapp.ui.model.TodoItem
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response
import javax.inject.Inject

class PosterService @Inject constructor(
    val application: ToDoApplication,
    val dao: DAO,
    val api: ToDoApiService
) {
    private var clientRevision: Int = 0

    private val _load = MutableLiveData<Boolean>()

    val load: LiveData<Boolean>
        get() = _load

    private val coroutineExceptionHandler =
        CoroutineExceptionHandler { _, throwable ->
            Log.e("Coroutine Exception Handler", "Error: ${throwable.message}")
        }

    private val job = Job()

    private val scope =
        CoroutineScope(Dispatchers.IO + job + coroutineExceptionHandler + CoroutineName("PosterServiceScope"))

    init {
        _load.value = false
        Log.d("LOAD STATE", "${_load.value}")
        updateData()
    }

    suspend fun insertToDo(item: TodoItem): NetworkResult<ElementResponse> {
        if (clientRevision != getServerRevision()) merge()
        return handleApi { api.addToDo(clientRevision, ToDoElement(item)) }
    }

    suspend fun deleteToDo(item: TodoItem): NetworkResult<ElementResponse> {
        if (clientRevision != getServerRevision()) merge()
        return handleApi { api.deleteToDo(clientRevision, item.id.toString()) }
    }

    suspend fun updateToDo(item: TodoItem): NetworkResult<ElementResponse> {
        if (clientRevision != getServerRevision()) merge()
        return handleApi {
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

    fun updateData() {
        if (application.isInternetAvailableAppField()) merge()
    }

    private fun merge() {
        scope.launch {
                withContext(Dispatchers.Main) {
                    _load.value = true
                }
                Log.d("LOAD STATE", "${_load.value}")

                val serverList = api.getAllToDo().body()?.list?.toMutableList() ?: mutableListOf()

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

                clientRevision = getServerRevision()

                withContext(Dispatchers.Main) {
                    _load.value = false
                }
                Log.d("LOAD STATE", "${_load.value}")
        }
    }

    private fun operationWithServerComplete() {
        ++clientRevision
    }

    private suspend fun <T : Any> handleApi(
        execute: suspend () -> Response<T>
    ): NetworkResult<T> {
        return try {
            val response = execute()
            val body = response.body()
            if (response.isSuccessful && body != null) {
                operationWithServerComplete()
                NetworkResult.Success(body)
            } else {
                NetworkResult.Error(code = response.code(), message = response.message())
            }
        } catch (e: HttpException) {
            NetworkResult.Error(code = e.code(), message = e.message())
        } catch (e: Throwable) {
            NetworkResult.Exception(e)
        }
    }
}

