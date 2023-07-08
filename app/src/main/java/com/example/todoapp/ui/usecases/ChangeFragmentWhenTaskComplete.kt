package com.example.todoapp.ui.usecases

import android.app.Activity
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import com.example.todoapp.R
import com.example.todoapp.ui.model.TodoItem
import com.example.todoapp.viewmodel.ToDoViewModel

class ChangeFragmentWhenTaskComplete(
    val activity: Activity,
    val rootView: View,
    val item: TodoItem,
    val viewModel: ToDoViewModel
    ){

    fun set() {
        val saveButton = rootView.findViewById<AppCompatButton>(R.id.save_button)

        saveButton.isClickable = false
        saveButton.text = activity.getString(R.string.task_complete)
        saveButton.setTextColor(
            ContextCompat.getColor(
                activity,
                R.color.color_light_green
            )
        )

        val untilDo = rootView.findViewById<TextView>(R.id.until_do)
        val editText = rootView.findViewById<AppCompatEditText>(R.id.edit_text)
        val deadlineSwitch = rootView.findViewById<SwitchCompat>(R.id.deadline_switch)

        untilDo.visibility =
            if (viewModel.changeDeadline != null) View.VISIBLE else View.GONE
        editText.isEnabled = false
        deadlineSwitch.visibility = View.GONE
    }
}