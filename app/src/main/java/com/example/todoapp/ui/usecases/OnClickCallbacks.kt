package com.example.todoapp.ui.usecases

import com.example.todoapp.ui.model.TodoItem

interface OnClickCallbacks {
    fun onItemClick(item: TodoItem)
}