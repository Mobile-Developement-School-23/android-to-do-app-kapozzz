package com.example.todoapp.ui.usecases

import android.app.Activity
import android.app.DatePickerDialog
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import com.example.todoapp.R
import com.example.todoapp.viewmodel.ToDoViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class InitDatePicker(
    val activity: Activity,
    val rootView: View,
    val viewModel: ToDoViewModel
) {

    fun set() {
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

            viewModel.changeDeadline = date
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
