package com.example.todoapp.ui.usecases

import android.app.Activity
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.todoapp.R
import com.example.todoapp.ui.model.Importance
import com.example.todoapp.ui.model.TodoItem
import com.example.todoapp.viewmodel.ToDoViewModel

class InitPopupMenu(
    private val activity: Activity,
    private val rootView: View,
    private val item: TodoItem?,
    private val viewModel: ToDoViewModel
) {
    fun set() {

        if (item != null && item.done) return

        val importanceButton = rootView.findViewById<TextView>(R.id.importance_button)
        val popupMenu =
            androidx.appcompat.widget.PopupMenu(activity, importanceButton)
        popupMenu.inflate(R.menu.popup_importance_layout)
        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.low_importance_btn -> {
                    importanceButton.text = activity.getString(R.string.no)
                    val color =
                        ContextCompat.getColor(activity.applicationContext, R.color.gray)
                    importanceButton.setTextColor(color)
                    viewModel.changeImportance = Importance.LOW
                    true
                }

                R.id.ordinary_importance_btn -> {
                    importanceButton.text = activity.getString(R.string.low)
                    val color =
                        ContextCompat.getColor(activity.applicationContext, R.color.gray)
                    importanceButton.setTextColor(color)
                    viewModel.changeImportance= Importance.BASIC
                    true
                }

                R.id.urgent_importance_btn -> {
                    importanceButton.text = activity.getString(R.string.urgent)
                    val color =
                        ContextCompat.getColor(activity.applicationContext, R.color.red)
                    importanceButton.setTextColor(color)
                    viewModel.changeImportance = Importance.IMPORTANT
                    true
                }

                else -> false
            }
        }

        importanceButton.setOnClickListener {
            popupMenu.show()
        }
    }
}