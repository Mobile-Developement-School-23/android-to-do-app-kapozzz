package com.example.todoapp.data.server

import com.example.todoapp.model.TodoItem

data class ListResponse(
    val list: List<TodoItem>,
    val revision: Int
)