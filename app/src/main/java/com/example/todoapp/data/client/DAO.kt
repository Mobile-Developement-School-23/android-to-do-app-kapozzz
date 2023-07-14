package com.example.todoapp.data.client

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.todoapp.ui.model.TodoItem

@Dao
interface DAO {

    @Query("SELECT EXISTS(SELECT 1 FROM TodoItems WHERE id = :itemId LIMIT 1)")
    fun checkIfItemExists(itemId: String): Boolean

    @Insert
    suspend fun insertToDo(newToDo: TodoItem)

    @Insert
    suspend fun insertListTodo(todoItems: List<TodoItem>)

    @Query("SELECT * FROM TodoItems")
    suspend fun getToDoItemsMerge(): List<TodoItem>?

    @Query("SELECT * FROM TodoItems")
    fun getToDoItems(): LiveData<List<TodoItem>?>

    @Update
    fun updateToDoItem(itemToUpdate: TodoItem)

    @Query("DELETE FROM TodoItems WHERE id =(:ToDoItemId)")
    fun deleteTodo(ToDoItemId: String)

    @Query("DELETE FROM TodoItems")
    fun deleteAllTodoItems()
}