package com.example.todoapp.data.model

import com.example.todoapp.ui.model.TodoItem

data class ListResponse(
    val list: List<TodoItem>,
    val revision: Int
)