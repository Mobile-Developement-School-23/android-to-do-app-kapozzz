package com.example.todoapp.model

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.todoapp.database.ToDoDB

// Предоставляет view model необходимые данные
class RepositoryToDo private constructor(context: Context) {

    private val database by lazy { ToDoDB.getDatabase(context) }
    private val dao by lazy { database.mainDAO() }

    // Добавить дело
    suspend fun insertToDo(newToDo: TodoItem) {
        dao.insertToDo(newToDo)
    }

    // Удалить дело
    fun deleteToDo(id: String) {
        dao.deleteTodo(id)
    }

    // Обновить дело
    fun updateToDo(toDoItemToUpdate: TodoItem) {
        dao.updateToDoItem(toDoItemToUpdate)
    }

    // Все дела в livedata что-бы подписаться
    fun getToDoItems(): LiveData<List<TodoItem>?> {
        return dao.getToDoItems()
    }

    // Всегда будет один экземляр репозитория
    companion object {
        val repository: RepositoryToDo? = null
        fun getInstance(context: Context): RepositoryToDo =
            this.repository ?: RepositoryToDo(context)
    }
}