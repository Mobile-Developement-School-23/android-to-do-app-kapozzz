package com.example.todoapp.ui

import android.app.Activity
import android.app.DatePickerDialog
import android.provider.ContactsContract.CommonDataKinds.Im
import android.text.Editable
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.findNavController
import com.example.todoapp.R
import com.example.todoapp.ui.model.Importance
import com.example.todoapp.ui.model.TodoItem
import com.example.todoapp.ui.usecases.InitDatePicker
import com.example.todoapp.ui.usecases.InitPopupMenu
import com.example.todoapp.viewmodel.ToDoViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject


class FragmentNewViewController @Inject constructor(
    private val activity: Activity,
    private val rootView: View,
    private val lifecycleOwner: LifecycleOwner,
    private val viewModel: ToDoViewModel,
) {

    private var importance: Importance
        get() = viewModel.changeImportance
        set(value) {viewModel.changeImportance = value}
    private var deadline: Date?
        get() = viewModel.changeDeadline
        set(value) {viewModel.changeDeadline = value}

    fun changeElements() {

        deadline = null
        importance = Importance.LOW

        InitPopupMenu(
            activity,
            rootView,
            null,
            viewModel
        ).set()

        InitDatePicker(
            activity,
            rootView,
            viewModel
        ).set()

        initOnClickListeners()

    }

    fun saveItemState() {
        val editText = rootView.findViewById<AppCompatEditText>(R.id.edit_text)
        val temp = editText.text.toString()
        val currentDate = Date()

        val itemForSave = TodoItem(
            id = UUID.randomUUID(),
            text = temp,
            importance = importance,
            deadline = deadline,
            done = false,
            created_at = Date(),
            changed_at = currentDate.time / 1000
        )
        viewModel.savedToDoItem = itemForSave
    }

    fun configurationChange() {
        val savedItem = viewModel.savedToDoItem

        savedItem?.deadline?.let { savedDeadline ->
            deadline = savedDeadline
            val converter = SimpleDateFormat("dd.MM.yyyy", Locale("ru", "RU"))
            val convertedDate = converter.format(savedDeadline)
            rootView.findViewById<SwitchCompat>(R.id.deadline_switch).isChecked = true
            val deadlineText = rootView.findViewById<EditText>(R.id.deadline_text)
            deadlineText.visibility = View.VISIBLE
            deadlineText.text = Editable.Factory.getInstance().newEditable(convertedDate)
        }
        importance = savedItem?.importance!!
        val importanceButton = rootView.findViewById<TextView>(R.id.importance_button)
        when (savedItem.importance) {
            Importance.LOW -> importanceButton.text = activity.getString(R.string.no)
            Importance.BASIC -> importanceButton.text = activity.getString(R.string.low)
            Importance.IMPORTANT -> importanceButton.text =
                activity.getString(R.string.urgent).also {
                    importanceButton.setTextColor(
                        ContextCompat.getColor(
                            activity,
                            R.color.color_light_red
                        )
                    )
                }
        }
    }

    private fun initOnClickListeners() {

        val closeButton = rootView.findViewById<ImageView>(R.id.close_button)
        closeButton.setOnClickListener {
            rootView.findNavController().popBackStack()
        }

        val saveButton = rootView.findViewById<AppCompatButton>(R.id.save_button)
        saveButton.setOnClickListener {
            saveButtonListener()
        }

    }

    private fun saveButtonListener(): Boolean {
        val editText = rootView.findViewById<AppCompatEditText>(R.id.edit_text)
        val temp = editText.text.toString()
        if (temp.trim().isEmpty()) {
            editText.error = activity.getString(R.string.empty_text_error)
            return true
        }

        val currentDate = Date()

        val completed = false
        val newToDo = TodoItem(
            text = temp,
            importance = importance,
            deadline = if (rootView.findViewById<SwitchCompat>(R.id.deadline_switch).isChecked) deadline else null,
            done = completed,
            created_at = Date(),
            changed_at = currentDate.time / 1000
        )
        viewModel.insertToDo(newToDo)
        rootView.findNavController().popBackStack()
        return true
    }
}



