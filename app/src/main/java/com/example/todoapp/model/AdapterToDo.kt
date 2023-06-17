package com.example.todoapp.model

import android.app.ActionBar.LayoutParams
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.R
import java.text.SimpleDateFormat
import java.util.Locale


class ToDoAdapter(val callbacks: onClickCallbacks) :
    androidx.recyclerview.widget.ListAdapter<TodoItem, ToDoAdapter.ToDoHolder>(ToDoDiffUtilCallback()) {

    var completedTasksIsVisible = true
        set(value) {
            field = value
            for ((position, task) in currentList.withIndex()) {
                if (task.isCompleted) notifyItemChanged(position)
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToDoHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_task_layout, parent, false)
        return ToDoHolder(view)
    }

    override fun onBindViewHolder(holder: ToDoHolder, position: Int) {
        val item = getItem(position)

        holder.itemView.setOnClickListener {
            callbacks.onItemClick(item)
        }

        // Скрывает выполненые задачи
        if (!completedTasksIsVisible && item.isCompleted) {
            val params = holder.itemView.layoutParams
            params.height = 0
            holder.itemView.layoutParams = params
            holder.itemView.visibility = View.GONE
            return
        }
        if (completedTasksIsVisible && item.isCompleted) {
            val params = holder.itemView.layoutParams
            params.height = LayoutParams.WRAP_CONTENT
            holder.itemView.layoutParams = params
            holder.itemView.visibility = View.VISIBLE
        }
        holder.onBind(item)
    }

    override fun getItem(position: Int): TodoItem {
        return currentList[position]
    }

    class ToDoHolder(view: View) : RecyclerView.ViewHolder(view) {

        val textView = view.findViewById<TextView>(R.id.item_text)
        val importance = view.findViewById<ImageView>(R.id.importance_image)
        val completed = view.findViewById<ImageView>(R.id.item_checkBox)
        val deadline = view.findViewById<TextView>(R.id.deadline_in_item)
        val dateTextView = view.findViewById<TextView>(R.id.data_text_view)

        fun onBind(item: TodoItem) {

            textView.text = item.text.trim()
            textView.setPaintFlags(textView.getPaintFlags())

            if (item.isCompleted) {
                completed.setImageResource(R.drawable.ic_complete_checked)
                importance.visibility = View.GONE
                deadline.visibility = View.GONE
                dateTextView.visibility = View.GONE
            } else {
                completed.setImageResource(R.drawable.ic_empty_checkbox)
                if (item.deadline != null) {

                    val converter = SimpleDateFormat("dd.MM.yyyy", Locale("ru", "RU"))
                    val convertedDeadline = item.deadline?.let { converter.format(it) }

                    deadline.text = convertedDeadline
                    deadline.visibility = View.VISIBLE
                    dateTextView.visibility = View.VISIBLE

                } else {
                    dateTextView.visibility = View.GONE
                    deadline.visibility = View.GONE
                }
                when (item.importance) {
                    Importance.URGENT -> {
                        with(importance) {
                            visibility = View.VISIBLE
                            setColorFilter(
                                ContextCompat.getColor(
                                    context,
                                    R.color.red
                                ),
                                PorterDuff.Mode.SRC_IN
                            )
                            setImageResource(R.drawable.ic_urgent_importance)
                        }
                        completed.setImageResource(R.drawable.ic_deadline_checkbox)
                    }

                    Importance.LOW -> {
                        with(importance) {
                            visibility = View.VISIBLE
                            setColorFilter(
                                ContextCompat.getColor(
                                    context,
                                    R.color.color_dark_gray
                                ),
                                PorterDuff.Mode.SRC_IN
                            )
                            setImageResource(R.drawable.ic_low_importance)
                        }
                    }

                    else -> {
                        importance.visibility = View.GONE
                    }
                }
            }
        }
    }
}

class ToDoDiffUtilCallback : DiffUtil.ItemCallback<TodoItem>() {
    override fun areItemsTheSame(oldItem: TodoItem, newItem: TodoItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: TodoItem, newItem: TodoItem): Boolean {
        return oldItem == newItem
    }
}

interface onClickCallbacks {
    fun onItemClick(item: TodoItem)
}
