package com.example.todoapp.ui.usecases

import android.app.Activity
import android.text.Editable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import com.example.todoapp.R
import com.example.todoapp.ui.model.TodoItem
import com.example.todoapp.viewmodel.ToDoViewModel
import java.text.SimpleDateFormat
import java.util.Locale

class SetDataInFragment(
    val activity: Activity,
    val rootView: View,
    val item: TodoItem,
    val viewModel: ToDoViewModel
) {

    fun set() {
        val editText = rootView.findViewById<AppCompatEditText>(R.id.edit_text)

        val editableTitle: Editable? =
            item.text.let { Editable.Factory.getInstance().newEditable(it) }
        editText.text = editableTitle

        val importanceButton = rootView.findViewById<TextView>(R.id.importance_button)
        viewModel.changeImportance = item.importance
        importanceButton.text = when (item.importance) {
            com.example.todoapp.ui.model.Importance.BASIC -> activity.getString(R.string.low)
            com.example.todoapp.ui.model.Importance.IMPORTANT -> activity.getString(R.string.urgent)
                .also {
                    importanceButton.setTextColor(
                        ContextCompat.getColor(
                            activity,
                            R.color.red
                        )
                    )
                }

            else -> activity.getString(R.string.no)
        }

        val converter = SimpleDateFormat("dd.MM.yy", Locale("ru", "RU"))
        val convertedDeadline = item.deadline?.let { converter.format(it) }
        val convertedCreationDate = converter.format(item.created_at)

        val deadlineText = rootView.findViewById<TextView>(R.id.deadline_text)
        val deadlineSwitch = rootView.findViewById<SwitchCompat>(R.id.deadline_switch)
        item.deadline?.let { itemDeadline ->
            viewModel.changeDeadline = itemDeadline
            deadlineText.visibility = android.view.View.VISIBLE
            deadlineText.text = convertedDeadline
            deadlineSwitch.isChecked = true
        }

        val creationDate = rootView.findViewById<TextView>(R.id.creation_date)
        val creationTextView = rootView.findViewById<TextView>(R.id.creation_textView)

        creationDate.text = convertedCreationDate
        creationTextView.visibility = View.VISIBLE

        val deleteButton = rootView.findViewById<LinearLayoutCompat>(R.id.delete_button)

        deleteButton.isClickable = true

        val deleteButtonImage = rootView.findViewById<ImageView>(R.id.delete_button_image)

        deleteButtonImage.setColorFilter(
            ContextCompat.getColor(
                activity,
                R.color.red
            )
        )

        val deleteButtonText = rootView.findViewById<TextView>(R.id.delete_button_text)

        deleteButtonText.setTextColor(
            ContextCompat.getColor(
                activity, R.color.red
            )
        )
    }

}