package com.example.todoapp.ui.usecases

import androidx.recyclerview.widget.DiffUtil
import com.example.todoapp.ui.model.TodoItem

class OnClickCallbacksImpl : DiffUtil.ItemCallback<TodoItem>() {
    override fun areItemsTheSame(oldItem: TodoItem, newItem: TodoItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: TodoItem, newItem: TodoItem): Boolean {
        return oldItem == newItem
    }
}
