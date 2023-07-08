package com.example.todoapp.ui

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.text.Editable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.findNavController
import com.example.todoapp.R
import com.example.todoapp.ui.model.Importance
import com.example.todoapp.ui.model.TodoItem
import com.example.todoapp.ui.usecases.ChangeFragmentWhenTaskComplete
import com.example.todoapp.ui.usecases.DeleteAlertDialog
import com.example.todoapp.ui.usecases.InitDatePicker
import com.example.todoapp.ui.usecases.InitPopupMenu
import com.example.todoapp.ui.usecases.SetDataInFragment
import com.example.todoapp.viewmodel.ToDoViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class FragmentChangeViewController @Inject constructor(
    private val activity: Activity,
    private val rootView: View,
    private val lifecycleOwner: LifecycleOwner,
    private val viewModel: ToDoViewModel,
) {

    private lateinit var item: TodoItem

    private var deadline: Date?
        get() = viewModel.changeDeadline
        set(value) {
            viewModel.changeDeadline = value
        }

    private var importance: Importance
        get() = viewModel.changeImportance
        set(value) {
            viewModel.changeImportance = value
        }

    fun setViews() {

        SetDataInFragment(
            activity,
            rootView,
            item,
            viewModel
        ).set()

        if (item.done) ChangeFragmentWhenTaskComplete(
            activity,
            rootView,
            item,
            viewModel
        ).set()

        InitDatePicker(
            activity,
            rootView,
            viewModel
        ).set()

        InitPopupMenu(
            activity,
            rootView,
            item,
            viewModel
        ).set()

        initListeners()

    }

    fun configurationChange() {
        item = viewModel.savedToDoItem ?: throw IllegalStateException("my error")
    }

    fun setArgumentsFromMain(todo: TodoItem) {
        item = todo
    }

    private fun initListeners() {

        val closeButton = rootView.findViewById<ImageView>(R.id.close_button)
        val deleteButton = rootView.findViewById<LinearLayoutCompat>(R.id.delete_button)
        val saveButton = rootView.findViewById<AppCompatButton>(R.id.save_button)

        closeButton.setOnClickListener {
            rootView.findNavController().popBackStack()
        }

        deleteButton.setOnClickListener {

            val deleteDialog = DeleteAlertDialog(
                activity,
                rootView,
                item,
                viewModel
            ).getDeleteDialog()

            deleteDialog?.show()

        }

        saveButton.setOnClickListener { saveButtonOnClick() }
    }

    private fun saveButtonOnClick(): Boolean {
        if (item.done) return true

        val editText = rootView.findViewById<AppCompatEditText>(R.id.edit_text)
        val temp = editText.text.toString()

        if (temp.trim().isEmpty()) {
            editText.error = activity.getString(R.string.empty_text_error)
            return true
        }

        val currentDate = Date()

        val newToDo = TodoItem(
            id = item.id,
            text = temp,
            importance = importance,
            deadline = deadline,
            done = false,
            created_at = item.created_at,
            changed_at = currentDate.time / 1000
        )
        viewModel.updateToDo(newToDo)
        rootView.findNavController().popBackStack()
        return true
    }

    fun saveToDoState() {
        val editText = rootView.findViewById<AppCompatEditText>(R.id.edit_text)
        val temp = editText.text.toString()
        val currentDate = Date()

        val itemForSave = TodoItem(
            id = item.id,
            text = temp,
            importance = importance,
            deadline = deadline,
            done = item.done,
            created_at = item.created_at,
            changed_at = currentDate.time / 1000
        )
        viewModel.savedToDoItem = itemForSave
    }
}