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

    private var deadline: Date? = null

    private var importance: Importance = Importance.LOW


    fun setViews() {

        initChangeLogic()

        initDatePicker()

        initPopUp()

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
            deleteToDo()
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

    private fun deleteToDo() {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(activity.getString(R.string.are_you_sure))
        builder.setPositiveButton(R.string.yes) { _, _ ->
            viewModel.deleteToDo(item)
            rootView.findNavController().popBackStack()
        }
        builder.setNegativeButton(R.string.no) { dialog, _ ->
            dialog.cancel()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun initChangeLogic() {

        setData()

        taskCompleted()

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

    private fun taskCompleted() {
        if (!item.done) return

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
            if (deadline != null) View.VISIBLE else View.GONE
        editText.isEnabled = false
        deadlineSwitch.visibility = View.GONE
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


    private fun setData() {
        val editText = rootView.findViewById<AppCompatEditText>(R.id.edit_text)

        val editableTitle: Editable? =
            item.text.let { Editable.Factory.getInstance().newEditable(it) }
        editText.text = editableTitle

        val importanceButton = rootView.findViewById<TextView>(R.id.importance_button)
        importance = item.importance
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
            deadline = itemDeadline
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