package com.example.todoapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.todoapp.model.TodoItem

// Доступ к базе данных
@Dao
interface DAO {

    // Добавить новую заметку
    @Insert
    suspend fun insertToDo(newToDo: TodoItem)

    // Получить все заметки
    @Query("SELECT * FROM TodoItems")
    fun getToDoItems(): LiveData<List<TodoItem>?>

    // Обновить заметку
    @Update
    fun updateToDoItem(itemToUpdate: TodoItem)

    // Удалить заметку
    @Query("DELETE FROM TodoItems WHERE id =(:ToDoItemId)")
    fun deleteTodo(ToDoItemId: String)
}