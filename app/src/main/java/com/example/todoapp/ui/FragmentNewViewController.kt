package com.example.todoapp.ui

import android.app.Activity
import android.app.DatePickerDialog
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

    private var importance: Importance = Importance.LOW
    private var deadline: Date? = null

    fun changeElements() {
        initPopUp()
        initDatePicker()
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

    private fun initPopUp() {
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
                    importance = Importance.LOW
                    true
                }

                R.id.ordinary_importance_btn -> {
                    importanceButton.text = activity.getString(R.string.low)
                    val color =
                        ContextCompat.getColor(activity.applicationContext, R.color.gray)
                    importanceButton.setTextColor(color)
                    importance = Importance.BASIC
                    true
                }

                R.id.urgent_importance_btn -> {
                    importanceButton.text = activity.getString(R.string.urgent)
                    val color =
                        ContextCompat.getColor(activity.applicationContext, R.color.red)
                    importanceButton.setTextColor(color)
                    importance = Importance.IMPORTANT
                    true
                }

                else -> false
            }
        }

        importanceButton.setOnClickListener {
            popupMenu.show()
        }
    }

    private fun initDatePicker() {
        val datePicker = DatePickerDialog(activity)
        val switch = rootView.findViewById<SwitchCompat>(R.id.deadline_switch)
        val deadlineText = rootView.findViewById<TextView>(R.id.deadline_text)

        datePicker.setOnCancelListener {
            switch.isChecked = false
        }

        datePicker.setOnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            val cal = Calendar.getInstance()
            cal.set(year, monthOfYear, dayOfMonth)
            val date = cal.time

            val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale("ru", "RU"))
            val formattedDate = dateFormat.format(date)

            deadline = date
            deadlineText.text = formattedDate
            deadlineText.visibility = View.VISIBLE
        }

        switch.setOnCheckedChangeListener { _, b ->
            if (b) {
                val cal = Calendar.getInstance()
                val day = cal.get(Calendar.DAY_OF_MONTH)
                val month = cal.get(Calendar.MONTH)
                val year = cal.get(Calendar.YEAR)
                datePicker.show()
                datePicker.updateDate(year, month, day)
            } else {
                deadlineText.visibility = View.GONE
            }
        }
    }
}



